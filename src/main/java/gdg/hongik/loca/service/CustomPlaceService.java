package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.place.CustomPlaceCreateRequest;
import gdg.hongik.loca.dto.place.CustomPlaceResponse;
import gdg.hongik.loca.dto.place.CustomPlaceUpdateRequest;
import gdg.hongik.loca.entity.CustomPlace;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.repository.CustomPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

// 사용자 생성 장소 도메인 서비스 계층
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomPlaceService {

    private final CustomPlaceRepository customPlaceRepository;

    // 사용자 생성 장소 생성
    @Transactional
    public CustomPlaceResponse create(
            Integer userId,
            CustomPlaceCreateRequest request
    ) {
        CustomPlace place = CustomPlace.builder()
                .userId(userId)
                .name(request.name())
                .address(request.address())
                .lat(request.lat())
                .lng(request.lng())
                .build();

        return CustomPlaceResponse.from(customPlaceRepository.save(place));
    }

    // 사용자 소유 활성 장소 목록 조회
    public List<CustomPlaceResponse> getPlaces(Integer userId) {
        return customPlaceRepository
                .findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(CustomPlaceResponse::from)
                .toList();
    }

    // 사용자 소유 활성 장소 단건 조회
    public CustomPlaceResponse getPlace(Integer userId, Integer placeId) {
        return CustomPlaceResponse.from(findActiveOwned(userId, placeId));
    }

    // 장소 수정 (dirty checking, save 미사용)
    @Transactional
    public CustomPlaceResponse updatePlace(
            Integer userId,
            Integer placeId,
            CustomPlaceUpdateRequest request
    ) {
        CustomPlace place = findActiveOwned(userId, placeId);

        place.setName(request.name());
        place.setAddress(request.address());
        place.setLat(request.lat());
        place.setLng(request.lng());

        return CustomPlaceResponse.from(place);
    }

    // 장소 소프트 삭제
    @Transactional
    public void deletePlace(Integer userId, Integer placeId) {
        findActiveOwned(userId, placeId)
                .setDeletedAt(OffsetDateTime.now());
    }

    // 사용자 소유 활성 장소 조회 헬퍼, 없으면 예외
    private CustomPlace findActiveOwned(Integer userId, Integer placeId) {
        return customPlaceRepository
                .findByPlaceIdAndUserIdAndDeletedAtIsNull(placeId, userId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));
    }
}
