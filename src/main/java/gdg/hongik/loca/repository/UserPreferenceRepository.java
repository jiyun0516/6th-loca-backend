package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.UserPreference;
import gdg.hongik.loca.entity.UserPreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, UserPreferenceId> {

    List<UserPreference> findByUserId(Integer userId);

    // 사용자 취향 점수 전체 삭제(재집계용)
    // - 벌크 삭제(즉시 실행): 재저장 시 insert-before-delete 충돌 방지
    // - 전체 삭제 후 재삽입 방식이라 "결과가 빈 경우 not in ()" 분기가 필요 없음
    @Modifying
    @Query("delete from UserPreference up where up.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}
