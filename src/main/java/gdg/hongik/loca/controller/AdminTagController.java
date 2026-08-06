package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.tag.TagCreateRequest;
import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 관리자용 태그 관리 API
// - 생성/삭제
@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    // POST /api/admin/tags - 태그 생성
    // - 태그 이름 중복이면 409
    // - 성공 시 201
    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagCreateRequest request) {
        TagResponse response = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // DELETE /api/admin/tags/{tagId} - 태그 삭제
    // - 태그가 없으면 404
    // - 리뷰에 사용 중이면 409
    // - 성공 시 204
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Integer tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
}
