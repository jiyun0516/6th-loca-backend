package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.Favorite;
import gdg.hongik.loca.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    List<Favorite> findByUserId(Integer userId);

    List<Favorite> findByPlaceId(Integer placeId);
}
