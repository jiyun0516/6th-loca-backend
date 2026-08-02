package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.place.PlaceCreateRequest;
import gdg.hongik.loca.dto.place.PlaceDetailResponse;
import gdg.hongik.loca.dto.place.PlaceResponse;
import gdg.hongik.loca.dto.place.PlaceUpdateRequest;
import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.entity.PublicPlace;
import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.exception.DuplicateKakaoPlaceIdException;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.repository.PlacePreferenceRepository;
import gdg.hongik.loca.repository.PublicPlaceRepository;
import gdg.hongik.loca.repository.TagRepository;
import gdg.hongik.loca.repository.VisitRecordRepository;
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
public class PublicPlaceService {

    private final PublicPlaceRepository placeRepository;
    private final PlacePreferenceRepository placePreferenceRepository;
    private final TagRepository tagRepository;
    private final VisitRecordRepository visitRecordRepository;

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


        PublicPlace place = PublicPlace.builder()
                .name(request.name())
                .kakaoPlaceId(request.kakaoPlaceId())
                .address(request.address())
                .lat(request.lat())
                .lng(request.lng())
                .build();

        return PlaceResponse.from(placeRepository.save(place));
    }

    // 장소 단건 상세 조회
    // - 태그 목록, 방문 수 포함
    // - 없으면 PlaceNotFoundException
    public PlaceDetailResponse getPlace(Integer placeId) {
        PublicPlace place = findById(placeId);
        List<TagResponse> tags = getPlaceTags(placeId);
        long visitCount = visitRecordRepository.countByPlaceId(placeId);
        return PlaceDetailResponse.of(place, tags, visitCount);
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
        PublicPlace place = findById(placeId);

        place.setName(request.name());
        place.setAddress(request.address());
        place.setLat(request.lat());
        place.setLng(request.lng());

        return PlaceResponse.from(place);
    }

    //
    // 장소를 삭제한다(소프트 삭제).
    //
    @Transactional
    public void deletePlace(Integer placeId) {
        PublicPlace place = findById(placeId);
        place.setDeletedAt(java.time.OffsetDateTime.now());
    }

    // 장소에 매핑된 태그 목록 조회
    // - place_preferences -> tags 조인
    private List<TagResponse> getPlaceTags(Integer placeId) {
        List<Integer> tagIds = placePreferenceRepository.findByPlaceId(placeId).stream()
                .map(PlacePreference::getTagId)
                .toList();
        return tagRepository.findAllById(tagIds).stream()
                .map(TagResponse::from)
                .toList();
    }

    private PublicPlace findById(Integer placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
    }
}
