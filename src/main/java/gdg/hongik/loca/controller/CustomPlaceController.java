package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.place.CustomPlaceCreateRequest;
import gdg.hongik.loca.dto.place.CustomPlaceResponse;
import gdg.hongik.loca.dto.place.CustomPlaceUpdateRequest;
import gdg.hongik.loca.service.CustomPlaceService;
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

// 사용자 생성 장소 API
@RestController
@RequestMapping("/api/places/custom")
@RequiredArgsConstructor
public class CustomPlaceController {

    private final CustomPlaceService customPlaceService;

    // 사용자 생성 장소 등록
    @PostMapping
    public ResponseEntity<CustomPlaceResponse> create(@AuthenticationPrincipal Integer userId, @Valid @RequestBody CustomPlaceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customPlaceService.create(userId, request));
    }

    // 사용자 생성 장소 목록 조회
    @GetMapping
    public List<CustomPlaceResponse> getPlaces(@AuthenticationPrincipal Integer userId) {
        return customPlaceService.getPlaces(userId);
    }

    // 사용자 생성 장소 단건 조회
    @GetMapping("/{placeId}")
    public CustomPlaceResponse getPlace(@AuthenticationPrincipal Integer userId, @PathVariable Integer placeId) {
        return customPlaceService.getPlace(userId, placeId);
    }

    // 사용자 생성 장소 수정
    @PutMapping("/{placeId}")
    public CustomPlaceResponse update(@AuthenticationPrincipal Integer userId, @PathVariable Integer placeId, @Valid @RequestBody CustomPlaceUpdateRequest request) {
        return customPlaceService.updatePlace(userId, placeId, request);
    }

    // 사용자 생성 장소 삭제
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Integer userId, @PathVariable Integer placeId) {
        customPlaceService.deletePlace(userId, placeId);
        return ResponseEntity.noContent().build();
    }
}
