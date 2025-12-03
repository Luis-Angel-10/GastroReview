package websiters.gastroreview.repository;

import websiters.gastroreview.model.FavoriteReview;
import websiters.gastroreview.model.FavoriteReviewId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface FavoriteReviewRepository extends JpaRepository<FavoriteReview, FavoriteReviewId> {
    List<FavoriteReview> findById_UserId(UUID userId);

    List<FavoriteReview> findById_ReviewId(UUID reviewId);

    boolean existsById_UserIdAndId_ReviewId(UUID userId, UUID reviewId);
}
