package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.common.SliceResponse;
import gdg.hongik.loca.dto.recommendation.PlaceScoreProjection;
import gdg.hongik.loca.dto.recommendation.RecommendationResponse;
import gdg.hongik.loca.entity.PublicPlace;
import gdg.hongik.loca.exception.InvalidRecommendationRequestException;
import gdg.hongik.loca.repository.PlacePreferenceRepository;
import gdg.hongik.loca.repository.PublicPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gdg.hongik.loca.dto.recommendation.ForYouStatusResponse;
import gdg.hongik.loca.repository.VisitRecordRepository;

import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.entity.UserPreference;
import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.exception.ForYouLockedException;
import gdg.hongik.loca.repository.UserPreferenceRepository;
import gdg.hongik.loca.dto.recommendation.ForYouRecommendationResponse;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Set;
import java.math.BigDecimal;

// 추천 도메인 서비스 계층
// - Explore: 선택 태그 조합으로 미방문 장소 탐색
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final PlacePreferenceRepository placePreferenceRepository;
    private final PublicPlaceRepository placeRepository;
    private final PlacePreferenceUpdater placePreferenceUpdater;
    private final VisitRecordRepository visitRecordRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final ForYouScoreCalculator forYouScoreCalculator;

    // Explore 페이지 크기
    // - 클라이언트가 지정하지 않음. 장소별 리뷰 조회와 같은 방침 (URL 은 page 하나로 끝남)
    private static final int EXPLORE_PAGE_SIZE = 20;

    // ForYou 해금 조건 리뷰 개수
    private static final int FOR_YOU_REQUIRED_REVIEW_COUNT = 3;

    // ForYou 추천 장소 최대 반환 개수
    private static final int FOR_YOU_LIMIT = 5;

    // Explore 추천
    // - tagIds 중 하나라도 가진 장소 후보(ANY)
    // - 소프트 삭제 장소, 사용자가 이미 방문한 장소 제외
    // - 선택 태그 점수 합계 내림차순, 동점은 placeId 오름차순
    // - tagIds 빈 값이면 InvalidRecommendationRequestException(400)
    //   (태그 미선택 진입은 필터 없는 전체 장소 목록이 되어 GET /api/places/public 과 겹치므로 열지 않음)
    // - 정렬/크기는 서버가 정함. 클라이언트는 page 만 넘김
    public SliceResponse<RecommendationResponse> explore(
            Integer userId,
            List<Integer> tagIds,
            int page
    ) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new InvalidRecommendationRequestException();
        }

        // dirty 장소 점수 재집계는 **첫 페이지에서만** 실행
        // - 재집계가 점수를 바꾸므로 스크롤 도중 실행되면 순위가 흔들려 중복/누락이 생김
        // - 스크롤 중에는 같은 스냅샷을 보고, 반영은 사용자가 탐색을 새로 시작할 때 이뤄짐
        if (page <= 0) {
            placePreferenceUpdater.refreshDirty();
        }

        // 음수 page 는 PageRequest 가 예외를 던져 500 이 되므로 0 으로 내림
        // 정렬은 @Query 의 order by 가 담당하므로 Sort 를 싣지 않음
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), EXPLORE_PAGE_SIZE);

        Slice<PlaceScoreProjection> scores = placePreferenceRepository.findExploreScores(
                tagIds, userId, pageRequest);

        // N+1 회피: placeId 배치 로드 후 Map 구성
        List<Integer> placeIds = scores.getContent().stream()
                .map(PlaceScoreProjection::getPlaceId)
                .toList();
        Map<Integer, PublicPlace> placeMap = placeRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(PublicPlace::getPlaceId, Function.identity()));

        // Slice.map 으로 변환해 hasNext 등 슬라이스 메타를 보존 (프로젝션 순서 유지)
        return SliceResponse.from(scores.map(s -> RecommendationResponse.of(
                placeMap.get(s.getPlaceId()))));
    }

    // ForYou 추천
    // - 현재 리뷰가 3개 이상인 사용자만 이용 가능
    // - 사용자와 장소의 태그 선호도 정규화 후 매칭 점수 계산
    // - 소프트 삭제 장소, 사용자가 이미 방문한 장소 제외
    // - 매칭 점수 내림차순 상위 5개

    // 현재 리뷰 개수를 기준으로 ForYou 잠금/해금 상태 조회
    public ForYouStatusResponse getForYouStatus(Integer userId) {
        long reviewCount = visitRecordRepository.countByUserId(userId);

        return ForYouStatusResponse.of(
                reviewCount,
                FOR_YOU_REQUIRED_REVIEW_COUNT
        );
    }

    // 현재 사용자 취향을 기준으로 미방문 장소 상위 5개 추천
    public List<ForYouRecommendationResponse> forYou(Integer userId) {
        long reviewCount = visitRecordRepository.countByUserId(userId);

        if (reviewCount < FOR_YOU_REQUIRED_REVIEW_COUNT) {
            throw new ForYouLockedException();
        }

        // 리뷰 변경으로 dirty 처리된 장소 선호도 최신화
        placePreferenceUpdater.refreshDirty();

        List<UserPreference> userPreferences =
                userPreferenceRepository.findByUserId(userId);

        List<PlacePreference> placePreferences =
                placePreferenceRepository.findAll();

        Map<Integer, BigDecimal> scoresByPlace =
                forYouScoreCalculator.calculate(
                        userPreferences,
                        placePreferences
                );

        Set<Integer> visitedPlaceIds =
                visitRecordRepository
                        .findByUserIdOrderByVisitedAtDesc(userId)
                        .stream()
                        .map(VisitRecord::getPlaceId)
                        .collect(Collectors.toSet());

        Map<Integer, PublicPlace> activePlaceMap =
                placeRepository.findAllByDeletedAtIsNull()
                        .stream()
                        .collect(Collectors.toMap(
                                PublicPlace::getPlaceId,
                                Function.identity()
                        ));

        Set<Integer> userTagIds =
                userPreferences.stream()
                        .map(UserPreference::getTagId)
                        .collect(Collectors.toSet());

        Map<Integer, Long> matchedTagCountsByPlace =
                placePreferences.stream()
                        .filter(preference ->
                                userTagIds.contains(preference.getTagId()))
                        .collect(Collectors.groupingBy(
                                PlacePreference::getPlaceId,
                                Collectors.counting()
                        ));

        return scoresByPlace.entrySet()
                .stream()
                .filter(entry ->
                        activePlaceMap.containsKey(entry.getKey()))
                .filter(entry ->
                        !visitedPlaceIds.contains(entry.getKey()))
                .sorted((left, right) -> {
                    int scoreComparison =
                            right.getValue().compareTo(left.getValue());

                    if (scoreComparison != 0) {
                        return scoreComparison;
                    }

                    long leftTagCount =
                            matchedTagCountsByPlace.getOrDefault(
                                    left.getKey(),
                                    0L
                            );

                    long rightTagCount =
                            matchedTagCountsByPlace.getOrDefault(
                                    right.getKey(),
                                    0L
                            );

                    int tagCountComparison =
                            Long.compare(rightTagCount, leftTagCount);

                    if (tagCountComparison != 0) {
                        return tagCountComparison;
                    }

                    return Integer.compare(
                            left.getKey(),
                            right.getKey()
                    );
                })
                .limit(FOR_YOU_LIMIT)
                .map(entry ->
                        ForYouRecommendationResponse.from(
                                activePlaceMap.get(entry.getKey())
                        ))
                .toList();
    }
}

