package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.PlaceList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceListRepository extends JpaRepository<PlaceList, Long> {

    // 사용자 목록 전체 (생성 순)
    List<PlaceList> findByUserIdOrderByCreatedAtAsc(Integer userId);

    // 소유권 확인 + 단건 조회. 타인 소유는 빈 값이 되어 404 로 이어짐
    Optional<PlaceList> findByListIdAndUserId(Long listId, Integer userId);

    // 공유 링크 조회. shareToken is null = 공유 안 함 이므로 이 조회로 철회 여부까지 함께 걸러짐
    Optional<PlaceList> findByShareToken(String shareToken);
}
