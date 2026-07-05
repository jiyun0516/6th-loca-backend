package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {

    List<VisitRecord> findByUserId(Integer userId);

    List<VisitRecord> findByPlaceId(Integer placeId);
}
