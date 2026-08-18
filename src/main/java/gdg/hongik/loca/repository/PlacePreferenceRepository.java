package gdg.hongik.loca.repository;

import gdg.hongik.loca.dto.recommendation.PlaceScoreProjection;
import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.entity.PlacePreferenceId;
import gdg.hongik.loca.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlacePreferenceRepository extends JpaRepository<PlacePreference, PlacePreferenceId> {

    // 장소 대표 태그 조회 (점수 상위 N개)
    // 동점 시 tagId 오름차순으로 설정
    @Query("select t from PlacePreference pp, Tag t " +
            "where pp.tagId = t.tagId and pp.placeId = :placeId " +
            "order by pp.score desc, t.tagId asc")
    List<Tag> findTopTagsByPlaceId(@Param("placeId") Integer placeId, Pageable pageable);

    // Explore 후보 점수 집계
    // - 선택 태그(tagIds) 중 하나라도 가진 장소 후보(ANY)
    // - 소프트 삭제 장소 제외 (PublicPlace.deletedAt is null)
    // - 사용자가 이미 방문한 장소 제외 (VisitRecord 존재 place)
    // - 선택 태그 점수 합계 내림차순
    // - **Slice 반환**: count 쿼리를 실행하지 않음
    //   group by 쿼리에 Page 를 붙이면 파생 count 에 group by 가 남아 여러 행이 반환되어 깨짐. countQuery 를 따로 쓰지 않으려 Slice 를 씀
    // - **동점 정렬(pp.placeId asc) 필수**: 점수 합계만으로 정렬하면 동점 그룹의 순서가 보장되지 않아
    //   페이지 경계에서 같은 장소가 중복 반환되거나 누락됨
    // - order by 가 쿼리 안에 있으므로 **Pageable 에 Sort 를 실지 말 것** (order by 가 두 번 붙음)
    @Query("select pp.placeId as placeId, sum(pp.score) as score " +
            "from PlacePreference pp " +
            "where pp.tagId in :tagIds " +
            "and pp.placeId in (select p.placeId from PublicPlace p where p.deletedAt is null) " +
            "and pp.placeId not in (select v.placeId from VisitRecord v where v.userId = :userId) " +
            "group by pp.placeId " +
            "order by sum(pp.score) desc, pp.placeId asc")
    Slice<PlaceScoreProjection> findExploreScores(@Param("tagIds") List<Integer> tagIds,
                                                  @Param("userId") Integer userId,
                                                  Pageable pageable);

    // 장소 태그 점수 전체 삭제(재집계용)
    // - 벌크 삭제(즉시 실행): 재저장 시 insert-before-delete 충돌 방지
    @Modifying
    @Query("delete from PlacePreference pp where pp.placeId = :placeId")
    void deleteByPlaceId(@Param("placeId") Integer placeId);
}
