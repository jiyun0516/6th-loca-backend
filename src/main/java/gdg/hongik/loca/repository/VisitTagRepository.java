package gdg.hongik.loca.repository;

import gdg.hongik.loca.dto.preference.TagScoreProjection;
import gdg.hongik.loca.entity.VisitTag;
import gdg.hongik.loca.entity.VisitTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitTagRepository extends JpaRepository<VisitTag, VisitTagId> {

    List<VisitTag> findByVisitId(Long visitId);

    // 목록 조회
    List<VisitTag> findByVisitIdIn(List<Long> visitIds);

    boolean existsByTagId(Integer tagId);

    // 방문 기록의 태그 전체 삭제(태그 교체용)
    // - 벌크 삭제(즉시 실행): 재저장 시 insert-before-delete 충돌 방지
    @Modifying
    @Query("delete from VisitTag vt where vt.visitId = :visitId")
    void deleteByVisitId(@Param("visitId") Long visitId);

    // 사용자 취향 점수 재집계용 집계
    // - count(*): 태그 반복 선택이 취향 강도
    // - VisitTag-VisitRecord 연관 매핑이 없어 조건 조인
    @Query("select vt.tagId as tagId, count(vt) as score " +
            "from VisitTag vt, VisitRecord v " +
            "where v.visitId = vt.visitId and v.userId = :userId " +
            "group by vt.tagId")
    List<TagScoreProjection> aggregateByUserId(@Param("userId") Integer userId);

    // 장소 태그 점수 재집계용 집계
    // - count(distinct user_id): 한 사람은 한 표(반복 방문 흡수)
    @Query("select vt.tagId as tagId, count(distinct v.userId) as score " +
            "from VisitTag vt, VisitRecord v " +
            "where v.visitId = vt.visitId and v.placeId = :placeId " +
            "group by vt.tagId")
    List<TagScoreProjection> aggregateByPlaceId(@Param("placeId") Integer placeId);
}
