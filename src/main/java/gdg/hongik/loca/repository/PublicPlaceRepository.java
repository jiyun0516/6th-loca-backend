package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.PublicPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicPlaceRepository extends JpaRepository<PublicPlace, Integer> {

    Optional<PublicPlace> findByKakaoPlaceId(String kakaoPlaceId);
}
