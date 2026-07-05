package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.entity.PlacePreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacePreferenceRepository extends JpaRepository<PlacePreference, PlacePreferenceId> {

    List<PlacePreference> findByPlaceId(Integer placeId);
}
