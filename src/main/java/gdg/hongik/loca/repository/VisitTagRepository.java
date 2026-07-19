package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.VisitTag;
import gdg.hongik.loca.entity.VisitTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitTagRepository extends JpaRepository<VisitTag, VisitTagId> {

    List<VisitTag> findByVisitId(Long visitId);

    List<VisitTag> findByTagId(Integer tagId);

    // 방문 기록의 태그 전체 삭제(태그 교체용)
    // - 벌크 삭제(즉시 실행): 재저장 시 insert-before-delete 충돌 방지
    @Modifying
    @Query("delete from VisitTag vt where vt.visitId = :visitId")
    void deleteByVisitId(@Param("visitId") Long visitId);
}
