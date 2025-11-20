package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import websiters.gastroreview.model.RestaurantSchedule;

import java.util.List;
import java.util.UUID;

public interface RestaurantScheduleRepository extends JpaRepository<RestaurantSchedule, UUID> {

    List<RestaurantSchedule> findByRestaurant_Id(UUID restaurantId);

    List<RestaurantSchedule> findByWeekday(Integer weekday);

    @Query("""
        SELECT rs
        FROM RestaurantSchedule rs
        WHERE rs.special = TRUE
    """)
    List<RestaurantSchedule> findSpecialSchedules();
}
