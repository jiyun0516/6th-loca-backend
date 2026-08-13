package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.placelist.*;
import gdg.hongik.loca.entity.CustomPlace;
import gdg.hongik.loca.entity.PlaceList;
import gdg.hongik.loca.entity.PlaceListItem;
import gdg.hongik.loca.entity.PlaceListItemId;
import gdg.hongik.loca.entity.PublicPlace;
import gdg.hongik.loca.exception.DuplicateListItemException;
import gdg.hongik.loca.exception.PlaceListItemNotFoundException;
import gdg.hongik.loca.exception.PlaceListNotFoundException;
import gdg.hongik.loca.exception.PlaceNotFoundException;
import gdg.hongik.loca.repository.CustomPlaceRepository;
import gdg.hongik.loca.repository.PlaceListItemRepository;
import gdg.hongik.loca.repository.PlaceListRepository;
import gdg.hongik.loca.repository.PublicPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 장소 목록 도메인 서비스 계층
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceListService {

    private final PlaceListRepository placeListRepository;
    private final PlaceListItemRepository placeListItemRepository;
    private final PublicPlaceRepository publicPlaceRepository;
    private final CustomPlaceRepository customPlaceRepository;

    // 목록 생성
    @Transactional
    public PlaceListResponse create(Integer userId, PlaceListCreateRequest request) {
        PlaceList list = PlaceList.builder()
                .userId(userId)
                .name(request.name())
                .build();

        return PlaceListResponse.of(placeListRepository.save(list), 0);
    }

    // 목록 전체 조회
    // - 쿼리 4회 고정 (목록 1 + 항목 1 + 장소 2). 목록마다 재조회하지 않음
    public List<PlaceListResponse> getLists(Integer userId) {
        List<PlaceList> lists = placeListRepository.findByUserIdOrderByCreatedAtAsc(userId);
        if (lists.isEmpty()) {
            return List.of();
        }

        List<Long> listIds = lists.stream().map(PlaceList::getListId).toList();
        List<PlaceListItem> items = placeListItemRepository.findByListIdIn(listIds);

        VisiblePlaces visible = loadVisiblePlaces(placeIdsOf(items), userId);

        Map<Long, Integer> counts = new HashMap<>();
        for (PlaceListItem item : items) {
            if (visible.contains(item.getPlaceId())) {
                counts.merge(item.getListId(), 1, Integer::sum);
            }
        }

        return lists.stream()
                .map(list -> PlaceListResponse.of(list, counts.getOrDefault(list.getListId(), 0)))
                .toList();
    }

    // 목록 상세 조회
    public PlaceListDetailResponse getList(Integer userId, Long listId) {
        PlaceList list = findOwned(userId, listId);

        List<PlaceListItem> items = placeListItemRepository.findByListIdOrderByCreatedAtAsc(listId);
        VisiblePlaces visible = loadVisiblePlaces(placeIdsOf(items), userId);

        List<PlaceListItemResponse> responses = new ArrayList<>();
        for (PlaceListItem item : items) {
            PlaceListItemResponse response = visible.toResponse(item);
            if (response != null) {
                responses.add(response);
            }
        }

        return PlaceListDetailResponse.of(list, responses, items.size() - responses.size());
    }

    // 목록 이름 수정 (dirty checking, save 미사용)
    @Transactional
    public PlaceListResponse updateList(Integer userId, Long listId, PlaceListUpdateRequest request) {
        PlaceList list = findOwned(userId, listId);
        list.setName(request.name());

        List<PlaceListItem> items = placeListItemRepository.findByListIdOrderByCreatedAtAsc(listId);
        VisiblePlaces visible = loadVisiblePlaces(placeIdsOf(items), userId);
        int itemCount = (int) items.stream().filter(i -> visible.contains(i.getPlaceId())).count();

        return PlaceListResponse.of(list, itemCount);
    }

    // 목록 삭제 (하드 삭제. 항목은 DB cascade)
    @Transactional
    public void deleteList(Integer userId, Long listId) {
        placeListRepository.delete(findOwned(userId, listId));
    }

    // 장소 담기
    @Transactional
    public void addItem(Integer userId, Long listId, PlaceListItemCreateRequest request) {
        findOwned(userId, listId);

        Integer placeId = request.placeId();
        assertPlaceAccessible(placeId, userId);

        if (placeListItemRepository.existsByListIdAndPlaceId(listId, placeId)) {
            throw new DuplicateListItemException(listId, placeId);
        }

        placeListItemRepository.save(
                PlaceListItem.builder()
                        .listId(listId)
                        .placeId(placeId)
                        .build()
        );
    }

    // 장소 빼기 (하드 삭제)
    @Transactional
    public void removeItem(Integer userId, Long listId, Integer placeId) {
        findOwned(userId, listId);

        PlaceListItem item = placeListItemRepository
                .findById(new PlaceListItemId(listId, placeId))
                .orElseThrow(() -> new PlaceListItemNotFoundException(listId, placeId));

        placeListItemRepository.delete(item);
    }

    // 소유 목록 조회 헬퍼. 타인 소유는 여기서 404 가 됨
    private PlaceList findOwned(Integer userId, Long listId) {
        return placeListRepository
                .findByListIdAndUserId(listId, userId)
                .orElseThrow(() -> new PlaceListNotFoundException(listId));
    }

    // 담을 수 있는 장소인지 검증
    // - CATALOG: 활성 행이면 허용
    // - CUSTOM: 내 장소이거나 공개 허용된 타인 장소만 허용
    // - 불충족은 전부 404. 403 이면 "그런 장소가 있긴 하다"를 흘림
    private void assertPlaceAccessible(Integer placeId, Integer userId) {
        if (publicPlaceRepository.existsByPlaceIdAndDeletedAtIsNull(placeId)) {
            return;
        }
        boolean custom = !customPlaceRepository
                .findOwnedOrShareableByPlaceIdIn(List.of(placeId), userId)
                .isEmpty();
        if (!custom) {
            throw new PlaceNotFoundException(placeId);
        }
    }

    private List<Integer> placeIdsOf(List<PlaceListItem> items) {
        return items.stream().map(PlaceListItem::getPlaceId).distinct().toList();
    }

    // 보이는 장소만 타입별로 적재
    // - 삭제되었거나 공개가 꺼진 장소는 애초에 담기지 않으므로 조회 시점에 사라짐
    // - 행은 지우지 않음. 공개가 복구되면 항목도 되살아나야 함
    private VisiblePlaces loadVisiblePlaces(Collection<Integer> placeIds, Integer userId) {
        if (placeIds.isEmpty()) {
            return new VisiblePlaces(Map.of(), Map.of());
        }
        return new VisiblePlaces(
                publicPlaceRepository.findByPlaceIdInAndDeletedAtIsNull(placeIds).stream()
                        .collect(Collectors.toMap(PublicPlace::getPlaceId, Function.identity())),
                customPlaceRepository.findOwnedOrShareableByPlaceIdIn(placeIds, userId).stream()
                        .collect(Collectors.toMap(CustomPlace::getPlaceId, Function.identity()))
        );
    }

    private record VisiblePlaces(
            Map<Integer, PublicPlace> publicPlaces,
            Map<Integer, CustomPlace> customPlaces
    ) {
        boolean contains(Integer placeId) {
            return publicPlaces.containsKey(placeId) || customPlaces.containsKey(placeId);
        }

        // 보이지 않으면 null. 호출부에서 건너뜀
        PlaceListItemResponse toResponse(PlaceListItem item) {
            PublicPlace pub = publicPlaces.get(item.getPlaceId());
            if (pub != null) {
                return PlaceListItemResponse.of(pub, item.getCreatedAt());
            }
            CustomPlace custom = customPlaces.get(item.getPlaceId());
            if (custom != null) {
                return PlaceListItemResponse.of(custom, item.getCreatedAt());
            }
            return null;
        }
    }
}
