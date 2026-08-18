package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.common.SliceResponse;
import gdg.hongik.loca.dto.review.PlaceReviewResponse;
import gdg.hongik.loca.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 장소별 리뷰 조회 API
@RestController
@RequestMapping("/api/places/{placeId}/reviews")
@RequiredArgsConstructor
public class PlaceReviewController {

    private final ReviewService reviewService;

    // GET /api/places/{placeId}/reviews - 장소별 리뷰 목록
    @GetMapping
    public SliceResponse<PlaceReviewResponse> listByPlace(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer placeId,
            @RequestParam(defaultValue = "0") int page
    ) {
        return reviewService.listByPlace(userId, placeId, page);
    }
}
