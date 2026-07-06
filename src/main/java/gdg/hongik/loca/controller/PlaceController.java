package gdg.hongik.loca.controller;

import gdg.hongik.loca.dto.place.PlaceDetailResponse;
import gdg.hongik.loca.dto.place.PlaceResponse;
import gdg.hongik.loca.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 장소 조회 API
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // GET /api/places - 장소 목록 조회
    @GetMapping
    public List<PlaceResponse> getPlaces() {
        return placeService.getPlaces();
    }

    // GET /api/places/{placeId} - 장소 상세 조회(태그, 평균 평점 포함)
    @GetMapping("/{placeId}")
    public PlaceDetailResponse getPlace(@PathVariable Integer placeId) {
        return placeService.getPlace(placeId);
    }
}
