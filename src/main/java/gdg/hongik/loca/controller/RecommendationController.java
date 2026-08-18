package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.common.SliceResponse;
import gdg.hongik.loca.dto.recommendation.RecommendationResponse;
import gdg.hongik.loca.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import gdg.hongik.loca.dto.recommendation.ForYouStatusResponse;
import gdg.hongik.loca.dto.recommendation.ForYouRecommendationResponse;

import java.util.List;

// 추천 API
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // GET /api/recommendations/explore?tagIds=1,2,3&page=0
    // - 선택 태그 ANY, 미방문, 점수합 내림차순 (동점은 placeId 오름차순)
    // - tagIds 누락 시 Spring 기본 400, 빈 값이면 서비스에서 400
    // - size 는 서버 고정. 클라이언트는 page 만 넘김 (미전달 시 0)
    @GetMapping("/explore")
    public SliceResponse<RecommendationResponse> explore(
            @AuthenticationPrincipal Integer userId,
            @RequestParam List<Integer> tagIds,
            @RequestParam(defaultValue = "0") int page
    ) {
        return recommendationService.explore(userId, tagIds, page);
    }

    // GET /api/recommendations/for-you/status
    // - 현재 리뷰 개수를 기준으로 ForYou 잠금/해금 상태 반환
    // - 리뷰 3개 이상이면 unlocked=true, 3개 미만이면 false
    @GetMapping("/for-you/status")
    public ForYouStatusResponse getForYouStatus(
            @AuthenticationPrincipal Integer userId
    ) {
        return recommendationService.getForYouStatus(userId);
    }

    // GET /api/recommendations/for-you
    // - 사용자와 장소의 태그 선호도를 기반으로 미방문 장소 추천
    // - 리뷰 3개 미만이면 403, 해금 상태이면 점수 내림차순 상위 5개 반환
    @GetMapping("/for-you")
    public List<ForYouRecommendationResponse> forYou(
            @AuthenticationPrincipal Integer userId
    ) {
        return recommendationService.forYou(userId);
    }
}
