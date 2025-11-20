package websiters.gastroreview.repository;

import websiters.gastroreview.model.FavoriteReview;
import websiters.gastroreview.model.FavoriteReviewId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FavoriteReviewRepository
        extends JpaRepository<FavoriteReview, FavoriteReviewId> {

    Page<FavoriteReview> findByIdUserId(UUID userId, Pageable pageable);

    Page<FavoriteReview> findByIdReviewId(UUID reviewId, Pageable pageable);

    boolean existsById(FavoriteReviewId id);

    void deleteById(FavoriteReviewId id);
}
