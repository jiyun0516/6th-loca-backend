package gdg.hongik.loca.repository;

import gdg.hongik.loca.dto.recommendation.PlaceScoreProjection;
import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.entity.PlacePreferenceId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlacePreferenceRepository extends JpaRepository<PlacePreference, PlacePreferenceId> {

    List<PlacePreference> findByPlaceId(Integer placeId);

    // Explore 후보 점수 집계
    // - 선택 태그(tagIds) 중 하나라도 가진 장소 후보(ANY)
    // - 소프트 삭제 장소 제외 (Place.deletedAt is null)
    // - 사용자가 이미 방문한 장소 제외 (활성 VisitRecord 존재 place)
    // - 선택 태그 점수 합계 내림차순, 상위 N개는 Pageable로 제한
    @Query("select pp.placeId as placeId, sum(pp.score) as score " +
            "from PlacePreference pp " +
            "where pp.tagId in :tagIds " +
            "and pp.placeId in (select p.placeId from Place p where p.deletedAt is null) " +
            "and pp.placeId not in (select v.placeId from VisitRecord v where v.userId = :userId and v.deletedAt is null) " +
            "group by pp.placeId " +
            "order by sum(pp.score) desc")
    List<PlaceScoreProjection> findExploreScores(@Param("tagIds") List<Integer> tagIds,
                                                 @Param("userId") Integer userId,
                                                 Pageable pageable);
}
