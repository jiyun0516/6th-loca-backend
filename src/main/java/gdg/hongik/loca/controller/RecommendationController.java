package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.recommendation.RecommendationResponse;
import gdg.hongik.loca.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

// 추천 API
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // GET /api/recommendations/explore?tagIds=1,2,3
    // - 선택 태그 ANY, 미방문, 점수합 내림차순 상위 20개
    // - tagIds 누락 시 Spring 기본 400, 빈 값이면 서비스에서 400
    @GetMapping("/explore")
    public List<RecommendationResponse> explore(
            @AuthenticationPrincipal Integer userId,
            @RequestParam List<Integer> tagIds
    ) {
        return recommendationService.explore(userId, tagIds);
    }
}
