package websiters.gastroreview.repository;

import websiters.gastroreview.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    Page<Alert> findByRestaurantId(UUID restaurantId, Pageable pageable);

    Page<Alert> findByReviewId(UUID reviewId, Pageable pageable);
}
