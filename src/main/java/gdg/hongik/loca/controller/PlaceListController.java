package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.placelist.PlaceListCreateRequest;
import gdg.hongik.loca.dto.placelist.PlaceListDetailResponse;
import gdg.hongik.loca.dto.placelist.PlaceListItemCreateRequest;
import gdg.hongik.loca.dto.placelist.PlaceListResponse;
import gdg.hongik.loca.dto.placelist.PlaceListUpdateRequest;
import gdg.hongik.loca.service.PlaceListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 장소 목록 API
@RestController
@RequestMapping("/api/users/me/lists")
@RequiredArgsConstructor
public class PlaceListController {

    private final PlaceListService placeListService;

    // 목록 생성
    @PostMapping
    public ResponseEntity<PlaceListResponse> create(@AuthenticationPrincipal Integer userId,
                                                    @Valid @RequestBody PlaceListCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(placeListService.create(userId, request));
    }

    // 목록 전체 조회
    @GetMapping
    public List<PlaceListResponse> getLists(@AuthenticationPrincipal Integer userId) {
        return placeListService.getLists(userId);
    }

    // 목록 상세 조회
    @GetMapping("/{listId}")
    public PlaceListDetailResponse getList(@AuthenticationPrincipal Integer userId,
                                           @PathVariable Long listId) {
        return placeListService.getList(userId, listId);
    }

    // 목록 이름 수정
    @PutMapping("/{listId}")
    public PlaceListResponse update(@AuthenticationPrincipal Integer userId,
                                    @PathVariable Long listId,
                                    @Valid @RequestBody PlaceListUpdateRequest request) {
        return placeListService.updateList(userId, listId, request);
    }

    // 목록 삭제
    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Integer userId,
                                       @PathVariable Long listId) {
        placeListService.deleteList(userId, listId);
        return ResponseEntity.noContent().build();
    }

    // 장소 담기
    @PostMapping("/{listId}/items")
    public ResponseEntity<Void> addItem(@AuthenticationPrincipal Integer userId,
                                        @PathVariable Long listId,
                                        @Valid @RequestBody PlaceListItemCreateRequest request) {
        placeListService.addItem(userId, listId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 장소 빼기
    @DeleteMapping("/{listId}/items/{placeId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal Integer userId,
                                           @PathVariable Long listId,
                                           @PathVariable Integer placeId) {
        placeListService.removeItem(userId, listId, placeId);
        return ResponseEntity.noContent().build();
    }
}
