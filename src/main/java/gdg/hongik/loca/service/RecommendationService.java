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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 추천 도메인 서비스 계층
// - Explore: 선택 태그 조합으로 미방문 장소 탐색
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final PlacePreferenceRepository placePreferenceRepository;
    private final PublicPlaceRepository placeRepository;
    private final PlacePreferenceUpdater placePreferenceUpdater;

    // 임시 userId, JWT 도입 시 토큰에서 추출로 교체
    private static final Integer TEMP_USER_ID = 1;

    // Explore 상위 개수
    private static final int EXPLORE_LIMIT = 20;

    // Explore 추천
    // - tagIds 중 하나라도 가진 장소 후보(ANY)
    // - 소프트 삭제 장소, 사용자가 이미 방문한 장소 제외
    // - 선택 태그 점수 합계 내림차순 상위 20개
    // - tagIds 빈 값이면 InvalidRecommendationRequestException(400)
    public List<RecommendationResponse> explore(List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new InvalidRecommendationRequestException();
        }

        // 조회 전 dirty 장소 점수 재집계 (place_preferences 갱신 시점)
        placePreferenceUpdater.refreshDirty();

        List<PlaceScoreProjection> scores = placePreferenceRepository.findExploreScores(
                tagIds, TEMP_USER_ID, PageRequest.of(0, EXPLORE_LIMIT));

        // N+1 회피: placeId 배치 로드 후 Map 구성
        List<Integer> placeIds = scores.stream()
                .map(PlaceScoreProjection::getPlaceId)
                .toList();
        Map<Integer, PublicPlace> placeMap = placeRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(PublicPlace::getPlaceId, Function.identity()));

        // 프로젝션 순서(점수 내림차순) 유지하며 매핑
        return scores.stream()
                .map(s -> RecommendationResponse.of(placeMap.get(s.getPlaceId()), s.getScore()))
                .toList();
    }
}
