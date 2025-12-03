package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import websiters.gastroreview.model.Rating;

import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByReviewId(UUID reviewId);

    List<Rating> findByUser_Id(UUID userId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.reviewId = :reviewId")
    Double getAverageStarsByReview(UUID reviewId);

}
