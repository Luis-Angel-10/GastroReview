package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import websiters.gastroreview.model.Dish;

import java.util.List;
import java.util.UUID;

public interface DishRepository extends JpaRepository<Dish, UUID> {

    List<Dish> findByRestaurant_Id(UUID restaurantId);

    List<Dish> findByNameContainingIgnoreCase(String name);

    @Query("""
        SELECT d
        FROM Dish d
        WHERE d.priceCents BETWEEN :min AND :max
    """)
    List<Dish> findByPriceRange(Integer min, Integer max);
}
