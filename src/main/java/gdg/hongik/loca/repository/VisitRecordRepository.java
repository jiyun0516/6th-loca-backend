package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {

    List<VisitRecord> findByUserId(Integer userId);

    List<VisitRecord> findByPlaceId(Integer placeId);

    // 사용자별 활성 기록 목록
    // - deletedAt null, visitedAt 내림차순
    List<VisitRecord> findByUserIdAndDeletedAtIsNullOrderByVisitedAtDesc(Integer userId);

    // 활성 + 소유 단건 조회
    // - 미존재/삭제됨/소유자 불일치 시 empty
    Optional<VisitRecord> findByVisitIdAndUserIdAndDeletedAtIsNull(Long visitId, Integer userId);

    // 장소 평균 평점 조회
    // - 삭제되지 않고 평점이 있는 기록만 집계
    // - 기록 없으면 null 반환
    @Query("select avg(v.rating) from VisitRecord v " +
            "where v.placeId = :placeId and v.deletedAt is null")
    Double findAverageRatingByPlaceId(@Param("placeId") Integer placeId);

    // 장소 유효 방문 수 조회
    // - 삭제되지 않은 기록만 집계
    @Query("select count(v) from VisitRecord v " +
            "where v.placeId = :placeId and v.deletedAt is null")
    long countActiveByPlaceId(@Param("placeId") Integer placeId);
}
