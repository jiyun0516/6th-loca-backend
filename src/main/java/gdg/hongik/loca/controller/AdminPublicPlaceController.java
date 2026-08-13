package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.place.PublicPlaceCreateRequest;
import gdg.hongik.loca.dto.place.PublicPlaceResponse;
import gdg.hongik.loca.dto.place.PublicPlaceUpdateRequest;
import gdg.hongik.loca.service.PublicPlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 관리자용 장소 관리 API
// - 등록/수정/삭제
@RestController
@RequestMapping("/api/admin/places")
@RequiredArgsConstructor
public class AdminPublicPlaceController {

    private final PublicPlaceService placeService;

    // POST /api/admin/places - 장소 등록
    // - 활성 장소와 kakaoPlaceId 중복이면 409
    // - 삭제된 장소와 kakaoPlaceId 중복이면 복구
    // - 성공 시 201
    @PostMapping
    public ResponseEntity<PublicPlaceResponse> create(@Valid @RequestBody PublicPlaceCreateRequest request) {
        PublicPlaceResponse response = placeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/admin/places/{placeId} - 장소 수정
    // - 전체 필드 교체
    // - 없으면 404
    @PutMapping("/{placeId}")
    public PublicPlaceResponse update(
            @PathVariable Integer placeId,
            @Valid @RequestBody PublicPlaceUpdateRequest request
    ) {
        return placeService.updatePlace(placeId, request);
    }

    // DELETE /api/admin/places/{placeId} - 장소 소프트 삭제
    // - 없으면 404
    // - 성공 시 204
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> delete(@PathVariable Integer placeId) {
        placeService.deletePlace(placeId);
        return ResponseEntity.noContent().build();
    }
}
