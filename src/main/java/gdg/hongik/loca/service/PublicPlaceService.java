package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.place.PlaceCreateRequest;
import gdg.hongik.loca.dto.place.PlaceDetailResponse;
import gdg.hongik.loca.dto.place.PlaceResponse;
import gdg.hongik.loca.dto.place.PlaceUpdateRequest;
import gdg.hongik.loca.dto.tag.TagResponse;
import gdg.hongik.loca.entity.PublicPlace;
import gdg.hongik.loca.exception.DuplicateKakaoPlaceIdException;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.repository.PlacePreferenceRepository;
import gdg.hongik.loca.repository.PublicPlaceRepository;
import gdg.hongik.loca.repository.VisitRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 공용 장소 도메인 서비스 계층
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicPlaceService {

    private final PublicPlaceRepository placeRepository;
    private final PlacePreferenceRepository placePreferenceRepository;
    private final VisitRecordRepository visitRecordRepository;

    // 장소 상세에 노출할 대표 태그 개수
    private static final int PLACE_TAG_LIMIT = 5;

    // 장소 생성
    // 활성 장소와 kakaoPlaceId가 겹칠 경우 DuplicateKakaoPlaceIdException 발생
    // 삭제된 장소와 kakaoPlaceId가 겹칠 경우 해당 장소 복구
    @Transactional
    public PlaceResponse create(PlaceCreateRequest request) {
        PublicPlace existing = placeRepository.findByKakaoPlaceId(request.kakaoPlaceId())
                .orElse(null);

        // 중복된 장소가 활성 장소일 경우 오류 발생
        if (existing != null && existing.getDeletedAt() == null) {
            throw new DuplicateKakaoPlaceIdException(request.kakaoPlaceId());
        }

        // 중복된 장소가 삭제된 장소일 경우 복구
        // place_id 유지: 기존 visit_records / place_preferences 연결 보존
        if (existing != null) {
            existing.setDeletedAt(null);
            existing.setName(request.name());
            existing.setAddress(request.address());
            existing.setLat(request.lat());
            existing.setLng(request.lng());
            return PlaceResponse.from(existing);
        }

        PublicPlace place = PublicPlace.builder()
                .name(request.name())
                .kakaoPlaceId(request.kakaoPlaceId())
                .address(request.address())
                .lat(request.lat())
                .lng(request.lng())
                .build();

        return PlaceResponse.from(placeRepository.save(place));
    }

    // 장소 단건 조회 (태그, 방문 횟수 포함)
    // 없으면 PlaceNotFoundException 발생
    public PlaceDetailResponse getPlace(Integer placeId) {
        PublicPlace place = findById(placeId);
        List<TagResponse> tags = getPlaceTags(placeId);
        long visitCount = visitRecordRepository.countByPlaceId(placeId);
        return PlaceDetailResponse.of(place, tags, visitCount);
    }

    // 활성 장소 목록 조회
    public List<PlaceResponse> getPlaces() {
        return placeRepository.findAllByDeletedAtIsNull().stream()
                .map(PlaceResponse::from)
                .toList();
    }

    // 장소 수정
    // dirty checking으로 반영 (save 미사용)
    @Transactional
    public PlaceResponse updatePlace(Integer placeId, PlaceUpdateRequest request) {
        PublicPlace place = findById(placeId);

        place.setName(request.name());
        place.setAddress(request.address());
        place.setLat(request.lat());
        place.setLng(request.lng());

        return PlaceResponse.from(place);
    }

    // 장소 소프트 삭제
    @Transactional
    public void deletePlace(Integer placeId) {
        PublicPlace place = findById(placeId);
        place.setDeletedAt(java.time.OffsetDateTime.now());
    }

    // 장소 대표 태그 조회
    // place_preferences 점수 상위 PLACE_TAG_LIMIT 개
    private List<TagResponse> getPlaceTags(Integer placeId) {
        return placePreferenceRepository
                .findTopTagsByPlaceId(placeId, PageRequest.of(0, PLACE_TAG_LIMIT))
                .stream()
                .map(TagResponse::from)
                .toList();
    }

    // 활성 장소 조회 헬퍼
    // 삭제된 장소 제외. 없으면 PlaceNotFoundException 발생
    // 상세/수정/삭제가 공유
    private PublicPlace findById(Integer placeId) {
        return placeRepository.findByPlaceIdAndDeletedAtIsNull(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
    }
}
