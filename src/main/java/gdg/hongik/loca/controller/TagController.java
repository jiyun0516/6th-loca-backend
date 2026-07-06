package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 태그 조회 API
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // GET /api/tags - 전체 태그 목록 조회
    @GetMapping
    public List<TagResponse> getTags() {
        return tagService.getTags();
    }
}
