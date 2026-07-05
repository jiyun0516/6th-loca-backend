package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.place.PlaceCreateRequest;
import gdg.hongik.loca.dto.place.PlaceResponse;
import gdg.hongik.loca.dto.place.PlaceUpdateRequest;
import gdg.hongik.loca.entity.Place;
import gdg.hongik.loca.exception.DuplicateKakaoPlaceIdException;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.repository.PlaceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 장소 도메인 서비스 계층.
 * 기본 CRUD를 담당한다.
 */
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;

    /**
     * 장소를 생성한다.
     * kakaoPlaceId가 이미 존재하면 {@link DuplicateKakaoPlaceIdException}을 던진다.
     */
    @Transactional
    public PlaceResponse create(@Valid PlaceCreateRequest request) {
        placeRepository.findByKakaoPlaceId(request.kakaoPlaceId())
                .ifPresent(existing -> {
                    throw new DuplicateKakaoPlaceIdException(request.kakaoPlaceId());
                });


        Place place = Place.builder()
                .name(request.name())
                .kakaoPlaceId(request.kakaoPlaceId())
                .address(request.address())
                .lat(request.lat())
                .lng(request.lng())
                .build();

        return PlaceResponse.from(placeRepository.save(place));
    }

    /**
     * 단건 조회. 없으면 {@link PlaceNotFoundException}.
     */
    public PlaceResponse getPlace(Integer placeId) {
        return PlaceResponse.from(findById(placeId));
    }

    /**
     * 전체 목록 조회.
     */
    public List<PlaceResponse> getPlaces() {
        return placeRepository.findAll().stream()
                .map(PlaceResponse::from)
                .toList();
    }

    /**
     * 장소를 수정한다. 변경 감지(dirty checking)로 반영한다.
     */
    @Transactional
    public PlaceResponse updatePlace(Integer placeId, @Valid PlaceUpdateRequest request) {
        Place place = findById(placeId);

        place.setName(request.name());
        place.setAddress(request.address());
        place.setLat(request.lat());
        place.setLng(request.lng());

        return PlaceResponse.from(place);
    }

    /**
     * 장소를 삭제한다(하드 삭제).
     */
    @Transactional
    public void deletePlace(Integer placeId) {
        Place place = findById(placeId);
        placeRepository.delete(place);
    }

    private Place findById(Integer placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
    }
}
