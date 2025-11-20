package websiters.gastroreview.repository;

import websiters.gastroreview.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    List<Restaurant> findByOwner_Id(UUID ownerId);

    @Query("""
        SELECT DISTINCT r
        FROM Restaurant r
        JOIN RestaurantAddress ra ON ra.restaurant = r
        JOIN Address a ON a = ra.address
        WHERE LOWER(a.city) = LOWER(:city)
    """)
    List<Restaurant> findByCityIgnoreCase(@Param("city") String city);

    @Query("""
        SELECT DISTINCT r
        FROM Restaurant r
        JOIN RestaurantAddress ra ON ra.restaurant = r
        WHERE ra.isPrimary = TRUE
    """)
    List<Restaurant> findAllWithPrimaryAddress();
}
