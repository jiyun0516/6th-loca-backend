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

    // 소유 확인 + 삭제되지 않은 사용자 생성 장소 존재 여부
    // - 리뷰 생성 시 장소 검증용. 엔티티가 필요 없어 exists 로 둠
    boolean existsByPlaceIdAndUserIdAndDeletedAtIsNull(Integer placeId, Integer userId);

    // 자신이 만들었거나 공개 허용된 커스텀 장소 목록 조회
    @Query("select c from CustomPlace c " +
            "where c.placeId in :placeIds and c.deletedAt is null " +
            "and (c.userId = :userId or c.isShareable = true)")
    List<CustomPlace> findOwnedOrShareableByPlaceIdIn(@Param("placeIds") Collection<Integer> placeIds,
                                                      @Param("userId") Integer userId);

    // 공개 허용된 커스텀 장소 목록 조회
    @Query("select c from CustomPlace c " +
            "where c.placeId in :placeIds and c.deletedAt is null and c.isShareable = true")
    List<CustomPlace> findShareableByPlaceIdIn(@Param("placeIds") Collection<Integer> placeIds);
}
