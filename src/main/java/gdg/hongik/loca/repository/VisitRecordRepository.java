package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {

    // 사용자별 기록 목록 (visitedAt 내림차순)
    // - 하드 삭제이므로 삭제 필터 불필요
    List<VisitRecord> findByUserIdOrderByVisitedAtDesc(Integer userId);

    // 소유 단건 조회
    // - 미존재/소유자 불일치 시 empty
    Optional<VisitRecord> findByVisitIdAndUserId(Long visitId, Integer userId);

    // 장소 방문 수
    long countByPlaceId(Integer placeId);

    // 사용자 리뷰 개수
    long countByUserId(Integer userId);
}
