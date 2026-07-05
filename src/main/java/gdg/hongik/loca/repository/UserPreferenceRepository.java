package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.UserPreference;
import gdg.hongik.loca.entity.UserPreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, UserPreferenceId> {

    List<UserPreference> findByUserId(Integer userId);
}
