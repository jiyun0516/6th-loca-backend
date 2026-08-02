package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.place.PrivatePlaceCreateRequest;
import gdg.hongik.loca.dto.place.PrivatePlaceResponse;
import gdg.hongik.loca.dto.place.PrivatePlaceUpdateRequest;
import gdg.hongik.loca.entity.PrivatePlace;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.repository.PrivatePlaceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.List;

// 개인 장소 도메인 서비스 계층
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivatePlaceService {

    private final PrivatePlaceRepository privatePlaceRepository;

    // 개인 장소 생성
    @Transactional
    public PrivatePlaceResponse create(
            Integer userId,
            @Valid PrivatePlaceCreateRequest request
    ) {
        PrivatePlace place = PrivatePlace.builder()
                .userId(userId)
                .name(request.name())
                .address(request.address())
                .lat(request.lat())
                .lng(request.lng())
                .build();

        return PrivatePlaceResponse.from(privatePlaceRepository.save(place));
    }

    // 사용자 소유 활성 장소 목록 조회
    public List<PrivatePlaceResponse> getPlaces(Integer userId) {
        return privatePlaceRepository
                .findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(PrivatePlaceResponse::from)
                .toList();
    }

    // 사용자 소유 활성 장소 단건 조회
    public PrivatePlaceResponse getPlace(Integer userId, Integer placeId) {
        return PrivatePlaceResponse.from(findActiveOwned(userId, placeId));
    }

    // 장소 수정 (dirty checking, save 미사용)
    @Transactional
    public PrivatePlaceResponse updatePlace(
            Integer userId,
            Integer placeId,
            @Valid PrivatePlaceUpdateRequest request
    ) {
        PrivatePlace place = findActiveOwned(userId, placeId);

        place.setName(request.name());
        place.setAddress(request.address());
        place.setLat(request.lat());
        place.setLng(request.lng());

        return PrivatePlaceResponse.from(place);
    }

    // 장소 소프트 삭제
    @Transactional
    public void deletePlace(Integer userId, Integer placeId) {
        findActiveOwned(userId, placeId)
                .setDeletedAt(OffsetDateTime.now());
    }

    // 사용자 소유 활성 장소 조회 헬퍼, 없으면 예외
    private PrivatePlace findActiveOwned(Integer userId, Integer placeId) {
        return privatePlaceRepository
                .findByPlaceIdAndUserIdAndDeletedAtIsNull(placeId, userId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
    }
}
