package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.CustomPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CustomPlaceRepository extends JpaRepository<CustomPlace, Integer> {

    // 사용자별 삭제되지 않은 사용자 생성 장소 목록 조회
    List<CustomPlace> findByUserIdAndDeletedAtIsNull(Integer userId);

    // 사용자 소유 확인 + 삭제되지 않은 사용자 생성 장소 단건 조회
    Optional<CustomPlace> findByPlaceIdAndUserIdAndDeletedAtIsNull(Integer placeId, Integer userId);

    // 내 장소이거나 공개 허용된 타인 장소
    // - 위 두 메서드와 달리 **소유자가 아닌 행도 반환**함. 소유자 경로에서 쓰지 말 것
    // - 소유자 없이 조회하는 메서드는 이름에 Shareable 을 포함할 것 (CONVENTIONS)
    @Query("select c from CustomPlace c " +
            "where c.placeId in :placeIds and c.deletedAt is null " +
            "and (c.userId = :userId or c.isShareable = true)")
    List<CustomPlace> findOwnedOrShareableByPlaceIdIn(@Param("placeIds") Collection<Integer> placeIds,
                                                      @Param("userId") Integer userId);
}
