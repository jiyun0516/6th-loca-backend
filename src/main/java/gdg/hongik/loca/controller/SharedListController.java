package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.placelist.SharedListResponse;
import gdg.hongik.loca.service.PlaceListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 공유 링크 조회 API (무인증)
// - 인증 대신 URL 의 43자 토큰이 자격증명 역할을 함. SecurityConfig 에서 GET /api/shared/** permitAll
// - 토큰이 URL 에 있으므로 Referer 유출·검색 색인 차단은 프론트 페이지 책임.
//   이 응답은 JSON 이라 여기에 Referrer-Policy 를 붙여도 실효가 없음
@RestController
@RequestMapping("/api/shared/lists")
@RequiredArgsConstructor
public class SharedListController {

    private final PlaceListService placeListService;

    @GetMapping("/{shareToken}")
    public SharedListResponse getSharedList(@PathVariable String shareToken) {
        return placeListService.getSharedList(shareToken);
    }
}
