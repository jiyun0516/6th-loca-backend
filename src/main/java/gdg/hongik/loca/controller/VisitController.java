package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.visit.VisitCreateRequest;
import gdg.hongik.loca.dto.visit.VisitDetailResponse;
import gdg.hongik.loca.dto.visit.VisitResponse;
import gdg.hongik.loca.dto.visit.VisitUpdateRequest;
import gdg.hongik.loca.service.VisitService;
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

// 내 방문 기록 API
@RestController
@RequestMapping("/api/users/me/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    // 임시 userId, JWT 도입 시 토큰에서 추출로 교체
    private static final Integer TEMP_USER_ID = 1;

    // POST /api/users/me/visits - 방문 기록 생성
    // - 성공 시 201
    @PostMapping
    public ResponseEntity<VisitResponse> create(@Valid @RequestBody VisitCreateRequest request) {
        VisitResponse response = visitService.create(TEMP_USER_ID, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/users/me/visits - 내 방문 기록 목록
    @GetMapping
    public List<VisitResponse> getMyVisits() {
        return visitService.getMyVisits(TEMP_USER_ID);
    }

    // GET /api/users/me/visits/{visitId} - 방문 기록 상세
    @GetMapping("/{visitId}")
    public VisitDetailResponse getMyVisit(@PathVariable Long visitId) {
        return visitService.getMyVisit(TEMP_USER_ID, visitId);
    }

    // PUT /api/users/me/visits/{visitId} - 방문 기록 수정
    @PutMapping("/{visitId}")
    public VisitResponse updateMyVisit(
            @PathVariable Long visitId,
            @Valid @RequestBody VisitUpdateRequest request
    ) {
        return visitService.updateMyVisit(TEMP_USER_ID, visitId, request);
    }

    // DELETE /api/users/me/visits/{visitId} - 방문 기록 삭제(soft-delete)
    // - 성공 시 204
    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> delete(@PathVariable Long visitId) {
        visitService.deleteMyVisit(TEMP_USER_ID, visitId);
        return ResponseEntity.noContent().build();
    }
}
