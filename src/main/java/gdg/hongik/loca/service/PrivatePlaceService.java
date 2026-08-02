package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.place.PrivatePlaceCreateRequest;
import gdg.hongik.loca.dto.place.PrivatePlaceResponse;
import gdg.hongik.loca.dto.place.PrivatePlaceUpdateRequest;
import gdg.hongik.loca.entity.PrivatePlace;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.repository.PrivatePlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

// 개인 장소 도메인 서비스 계층
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivatePlaceService {

    private final PrivatePlaceRepository privatePlaceRepository;

    // 임시 userId, JWT 도입 시 토큰 추출로 교체
    private static final Integer TEMP_USER_ID = 1;

    // 개인 장소 생성
    @Transactional
    public PrivatePlaceResponse create(PrivatePlaceCreateRequest request) {
        PrivatePlace place = PrivatePlace.builder()
                .userId(TEMP_USER_ID)
                .name(request.name())
                .address(request.address())
                .lat(request.lat())
                .lng(request.lng())
                .build();
        return PrivatePlaceResponse.from(privatePlaceRepository.save(place));
    }

    // 사용자 소유 활성 장소 목록 조회
    public List<PrivatePlaceResponse> getPlaces() {
        return privatePlaceRepository.findByUserIdAndDeletedAtIsNull(TEMP_USER_ID).stream()
                .map(PrivatePlaceResponse::from)
                .toList();
    }

    // 사용자 소유 활성 장소 단건 조회
    public PrivatePlaceResponse getPlace(Integer placeId) {
        return PrivatePlaceResponse.from(findActiveOwned(placeId));
    }

    // 장소 수정 (dirty checking, save 미사용)
    @Transactional
    public PrivatePlaceResponse updatePlace(Integer placeId, PrivatePlaceUpdateRequest request) {
        PrivatePlace place = findActiveOwned(placeId);
        place.setName(request.name());
        place.setAddress(request.address());
        place.setLat(request.lat());
        place.setLng(request.lng());
        return PrivatePlaceResponse.from(place);
    }

    // 장소 소프트 삭제
    @Transactional
    public void deletePlace(Integer placeId) {
        findActiveOwned(placeId).setDeletedAt(OffsetDateTime.now());
    }

    // 사용자 소유 활성 장소 조회 헬퍼, 없으면 예외
    private PrivatePlace findActiveOwned(Integer placeId) {
        return privatePlaceRepository.findByPlaceIdAndUserIdAndDeletedAtIsNull(placeId, TEMP_USER_ID)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
    }
}
