package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import websiters.gastroreview.model.RestaurantImage;

import java.util.List;
import java.util.UUID;

public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, UUID> {

    List<RestaurantImage> findByRestaurant_Id(UUID restaurantId);

    @Query("""
        SELECT ri 
        FROM RestaurantImage ri
        WHERE LOWER(ri.altText) LIKE LOWER(CONCAT('%', :text, '%'))
    """)
    List<RestaurantImage> searchByAltText(String text);
}
