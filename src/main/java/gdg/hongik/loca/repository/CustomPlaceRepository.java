package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.CustomPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomPlaceRepository extends JpaRepository<CustomPlace, Integer> {

    // 사용자별 삭제되지 않은 사용자 생성 장소 목록 조회
    List<CustomPlace> findByUserIdAndDeletedAtIsNull(Integer userId);

    // 사용자 소유 확인 + 삭제되지 않은 사용자 생성 장소 단건 조회
    Optional<CustomPlace> findByPlaceIdAndUserIdAndDeletedAtIsNull(Integer placeId, Integer userId);
}
