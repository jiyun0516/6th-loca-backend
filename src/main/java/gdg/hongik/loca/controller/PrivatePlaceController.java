package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.place.PrivatePlaceCreateRequest;
import gdg.hongik.loca.dto.place.PrivatePlaceResponse;
import gdg.hongik.loca.dto.place.PrivatePlaceUpdateRequest;
import gdg.hongik.loca.service.PrivatePlaceService;
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

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

// 개인 장소 API
@RestController
@RequestMapping("/api/places/private")
@RequiredArgsConstructor
public class PrivatePlaceController {

    private final PrivatePlaceService privatePlaceService;

    // 개인 장소 등록
    @PostMapping
    public ResponseEntity<PrivatePlaceResponse> create(@AuthenticationPrincipal Integer userId, @Valid @RequestBody PrivatePlaceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(privatePlaceService.create(userId, request));
    }

    // 개인 장소 목록 조회
    @GetMapping
    public List<PrivatePlaceResponse> getPlaces(@AuthenticationPrincipal Integer userId) {
        return privatePlaceService.getPlaces(userId);
    }

    // 개인 장소 단건 조회
    @GetMapping("/{placeId}")
    public PrivatePlaceResponse getPlace(@AuthenticationPrincipal Integer userId, @PathVariable Integer placeId) {
        return privatePlaceService.getPlace(userId, placeId);
    }

    // 개인 장소 수정
    @PutMapping("/{placeId}")
    public PrivatePlaceResponse update(@AuthenticationPrincipal Integer userId, @PathVariable Integer placeId, @Valid @RequestBody PrivatePlaceUpdateRequest request) {
        return privatePlaceService.updatePlace(userId, placeId, request);
    }

    // 개인 장소 삭제
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Integer userId, @PathVariable Integer placeId) {
        privatePlaceService.deletePlace(userId, placeId);
        return ResponseEntity.noContent().build();
    }
}
