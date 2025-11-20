package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import websiters.gastroreview.model.ReviewImage;

import java.util.List;
import java.util.UUID;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, UUID> {

    List<ReviewImage> findByReview_Id(UUID reviewId);
}
