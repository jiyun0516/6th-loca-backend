package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.PublicPlace;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface PublicPlaceRepository extends JpaRepository<PublicPlace, Integer> {

    Optional<PublicPlace> findByKakaoPlaceId(String kakaoPlaceId);

    List<PublicPlace> findAllByDeletedAtIsNull();

    Optional<PublicPlace> findByPlaceIdAndDeletedAtIsNull(Integer placeId);

    boolean existsByPlaceIdAndDeletedAtIsNull(Integer placeId);

    // 재집계 대상 표시
    @Modifying
    @Query("update PublicPlace p set p.preferenceDirtyAt = :now where p.placeId = :placeId")
    void markPreferenceDirty(@Param("placeId") Integer placeId, @Param("now") OffsetDateTime now);

    // 재집계 대상 목록
    // - 삭제된 장소는 제외. 표시는 남으므로 복구되면 다시 잡힘
    @Query("select p.placeId from PublicPlace p " +
            "where p.preferenceDirtyAt is not null and p.deletedAt is null")
    List<Integer> findDirtyPlaceIds();

    // 재집계 소유권 주장용 잠금 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PublicPlace p where p.placeId = :placeId")
    Optional<PublicPlace> findByPlaceIdForUpdate(@Param("placeId") Integer placeId);
}
