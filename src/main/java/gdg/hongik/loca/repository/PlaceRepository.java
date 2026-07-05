package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    Optional<Place> findByKakaoPlaceId(String kakaoPlaceId);
}
