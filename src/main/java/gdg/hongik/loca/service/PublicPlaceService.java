package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.place.PublicPlaceCreateRequest;
import gdg.hongik.loca.dto.place.PublicPlaceDetailResponse;
import gdg.hongik.loca.dto.place.PublicPlaceResponse;
import gdg.hongik.loca.dto.place.PublicPlaceUpdateRequest;
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
import java.time.OffsetDateTime;

// 공용 장소 도메인 서비스 계층
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicPlaceService {

    private final PublicPlaceRepository placeRepository;
    private final PlacePreferenceRepository placePreferenceRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final ImageStorageService imageStorageService;

    // 장소 상세에 노출할 대표 태그 개수
    private static final int PLACE_TAG_LIMIT = 5;

    // 장소 생성
    // 활성 장소와 kakaoPlaceId가 겹칠 경우 DuplicateKakaoPlaceIdException 발생
    // 삭제된 장소와 kakaoPlaceId가 겹칠 경우 해당 장소 복구
    @Transactional
    public PublicPlaceResponse create(PublicPlaceCreateRequest request) {
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

            if (request.imageUrl() != null
                    && !request.imageUrl().equals(existing.getImageUrl())) {

                String oldImageUrl = existing.getImageUrl();
                existing.setImageUrl(request.imageUrl());

                imageStorageService.deleteByUrl(oldImageUrl);
            }

            return PublicPlaceResponse.from(existing);
        }

        PublicPlace place = PublicPlace.builder()
                .name(request.name())
                .kakaoPlaceId(request.kakaoPlaceId())
                .address(request.address())
                .lat(request.lat())
                .lng(request.lng())
                .imageUrl(request.imageUrl())
                .build();

        return PublicPlaceResponse.from(placeRepository.save(place));
    }

    // 장소 단건 조회 (태그, 방문 횟수 포함)
    // 없으면 PlaceNotFoundException 발생
    public PublicPlaceDetailResponse getPlace(Integer placeId) {
        PublicPlace place = findById(placeId);
        List<TagResponse> tags = getPlaceTags(placeId);
        long visitCount = visitRecordRepository.countByPlaceId(placeId);
        return PublicPlaceDetailResponse.of(place, tags, visitCount);
    }

    // 활성 장소 목록 조회
    public List<PublicPlaceResponse> getPlaces() {
        return placeRepository.findAllByDeletedAtIsNull().stream()
                .map(PublicPlaceResponse::from)
                .toList();
    }

    // 장소 수정
    // dirty checking으로 반영 (save 미사용)
    @Transactional
    public PublicPlaceResponse updatePlace(Integer placeId, PublicPlaceUpdateRequest request) {
        PublicPlace place = findById(placeId);

        place.setName(request.name());
        place.setAddress(request.address());
        place.setLat(request.lat());
        place.setLng(request.lng());

        if (request.imageUrl() != null
                && !request.imageUrl().equals(place.getImageUrl())) {

            String oldImageUrl = place.getImageUrl();
            place.setImageUrl(request.imageUrl());

            imageStorageService.deleteByUrl(oldImageUrl);
        }

        return PublicPlaceResponse.from(place);
    }

    // 장소 소프트 삭제
    @Transactional
    public void deletePlace(Integer placeId) {
        PublicPlace place = findById(placeId);
        String oldImageUrl = place.getImageUrl();

        place.setImageUrl(null);
        place.setDeletedAt(OffsetDateTime.now());

        imageStorageService.deleteByUrl(oldImageUrl);
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
