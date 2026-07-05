package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.VisitTag;
import gdg.hongik.loca.entity.VisitTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitTagRepository extends JpaRepository<VisitTag, VisitTagId> {

    List<VisitTag> findByVisitId(Long visitId);

    List<VisitTag> findByTagId(Integer tagId);
}
