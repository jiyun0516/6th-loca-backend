package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.PlaceListItem;
import gdg.hongik.loca.entity.PlaceListItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PlaceListItemRepository extends JpaRepository<PlaceListItem, PlaceListItemId> {

    // 목록 상세용 (담은 순)
    List<PlaceListItem> findByListIdOrderByCreatedAtAsc(Long listId);

    // 목록 목록 화면의 항목 수 계산용. 목록별 재조회(N+1) 대신 한 번에 가져와 자바에서 묶음
    List<PlaceListItem> findByListIdIn(Collection<Long> listIds);

    boolean existsByListIdAndPlaceId(Long listId, Integer placeId);
}
