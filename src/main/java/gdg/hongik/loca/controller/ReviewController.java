package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.review.ReviewCreateRequestDto;
import gdg.hongik.loca.dto.review.ReviewResponseDto;
import gdg.hongik.loca.dto.review.ReviewUpdateRequest;
import gdg.hongik.loca.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 내 방문 후기(리뷰) API
@RestController
@RequestMapping("/api/users/me/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // POST /api/users/me/reviews - 후기 생성
    // - 성공 시 201
    @PostMapping
    public ResponseEntity<ReviewResponseDto> create(@Valid @RequestBody ReviewCreateRequestDto request) {
        ReviewResponseDto response = reviewService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/users/me/reviews - 내 후기 목록
    @GetMapping
    public List<ReviewResponseDto> list() {
        return reviewService.list();
    }

    // GET /api/users/me/reviews/{visitId} - 후기 상세
    @GetMapping("/{visitId}")
    public ReviewResponseDto detail(@PathVariable Long visitId) {
        return reviewService.detail(visitId);
    }

    // PUT /api/users/me/reviews/{visitId} - 후기 수정
    @PutMapping("/{visitId}")
    public ReviewResponseDto update(
            @PathVariable Long visitId,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        return reviewService.update(visitId, request);
    }

    // DELETE /api/users/me/reviews/{visitId} - 후기 삭제(soft-delete)
    // - 성공 시 204
    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> delete(@PathVariable Long visitId) {
        reviewService.delete(visitId);
        return ResponseEntity.noContent().build();
    }
}
