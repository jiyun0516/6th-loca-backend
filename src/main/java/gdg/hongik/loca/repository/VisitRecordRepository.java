package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {

    List<VisitRecord> findByUserId(Integer userId);

    List<VisitRecord> findByPlaceId(Integer placeId);

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
