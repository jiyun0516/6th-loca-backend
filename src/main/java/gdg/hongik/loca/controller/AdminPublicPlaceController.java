package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.place.PlaceCreateRequest;
import gdg.hongik.loca.dto.place.PlaceResponse;
import gdg.hongik.loca.dto.place.PlaceUpdateRequest;
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
    // - kakaoPlaceId 중복이면 409
    // - 성공 시 201
    @PostMapping
    public ResponseEntity<PlaceResponse> create(@Valid @RequestBody PlaceCreateRequest request) {
        PlaceResponse response = placeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/admin/places/{placeId} - 장소 수정
    // - 전체 필드 교체
    // - 없으면 404
    @PutMapping("/{placeId}")
    public PlaceResponse update(
            @PathVariable Integer placeId,
            @Valid @RequestBody PlaceUpdateRequest request
    ) {
        return placeService.updatePlace(placeId, request);
    }

    // DELETE /api/admin/places/{placeId} - 장소 삭제(하드 삭제)
    // - 없으면 404
    // - 성공 시 204
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> delete(@PathVariable Integer placeId) {
        placeService.deletePlace(placeId);
        return ResponseEntity.noContent().build();
    }
}
