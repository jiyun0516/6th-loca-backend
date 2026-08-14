package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.recommendation.PlaceScoreProjection;
import gdg.hongik.loca.dto.recommendation.RecommendationResponse;
import gdg.hongik.loca.entity.PublicPlace;
import gdg.hongik.loca.exception.InvalidRecommendationRequestException;
import gdg.hongik.loca.repository.PlacePreferenceRepository;
import gdg.hongik.loca.repository.PublicPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    // Explore 상위 개수
    private static final int EXPLORE_LIMIT = 20;

    // ForYou 해금 조건 리뷰 개수
    private static final int FOR_YOU_REQUIRED_REVIEW_COUNT = 3;

    // ForYou 추천 장소 최대 반환 개수
    private static final int FOR_YOU_LIMIT = 5;

    // Explore 추천
    // - tagIds 중 하나라도 가진 장소 후보(ANY)
    // - 소프트 삭제 장소, 사용자가 이미 방문한 장소 제외
    // - 선택 태그 점수 합계 내림차순 상위 20개
    // - tagIds 빈 값이면 InvalidRecommendationRequestException(400)
    public List<RecommendationResponse> explore(
            Integer userId,
            List<Integer> tagIds
    ) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new InvalidRecommendationRequestException();
        }

        // 조회 전 dirty 장소 점수 재집계 (place_preferences 갱신 시점)
        placePreferenceUpdater.refreshDirty();

        List<PlaceScoreProjection> scores = placePreferenceRepository.findExploreScores(
                tagIds, userId, PageRequest.of(0, EXPLORE_LIMIT));

        // N+1 회피: placeId 배치 로드 후 Map 구성
        List<Integer> placeIds = scores.stream()
                .map(PlaceScoreProjection::getPlaceId)
                .toList();
        Map<Integer, PublicPlace> placeMap = placeRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(PublicPlace::getPlaceId, Function.identity()));

        // 프로젝션 순서(점수 내림차순) 유지하며 매핑
        return scores.stream()
                .map(s -> RecommendationResponse.of(
                        placeMap.get(s.getPlaceId())
                ))
                .toList();
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

