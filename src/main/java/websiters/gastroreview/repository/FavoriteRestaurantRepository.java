package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import websiters.gastroreview.model.FavoriteRestaurant;
import websiters.gastroreview.model.FavoriteRestaurantId;

import java.util.List;
import java.util.UUID;

public interface FavoriteRestaurantRepository extends JpaRepository<FavoriteRestaurant, FavoriteRestaurantId> {

    List<FavoriteRestaurant> findByUser_Id(UUID userId);

    List<FavoriteRestaurant> findByRestaurant_Id(UUID restaurantId);

    boolean existsByUser_IdAndRestaurant_Id(UUID userId, UUID restaurantId);

}
