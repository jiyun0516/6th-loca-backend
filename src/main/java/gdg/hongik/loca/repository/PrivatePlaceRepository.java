package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.PrivatePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrivatePlaceRepository extends JpaRepository<PrivatePlace, Integer> {

    // 사용자별 삭제되지 않은 개인 장소 목록 조회
    List<PrivatePlace> findByUserIdAndDeletedAtIsNull(Integer userId);

    // 사용자 소유 확인 + 삭제되지 않은 개인 장소 단건 조회
    Optional<PrivatePlace> findByPlaceIdAndUserIdAndDeletedAtIsNull(Integer placeId, Integer userId);
}
