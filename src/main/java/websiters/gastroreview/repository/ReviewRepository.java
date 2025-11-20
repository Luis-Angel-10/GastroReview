package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import websiters.gastroreview.model.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findByRestaurant_Id(UUID restaurantId);

    List<Review> findByAuthor_Id(UUID userId);

    List<Review> findByDish_Id(UUID dishId);

    List<Review> findByHasImageTrue();

    List<Review> findByHasAudioTrue();

    @Query("""
        SELECT r
        FROM Review r
        WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(r.content) LIKE LOWER(CONCAT('%', :text, '%'))
    """)
    List<Review> searchByText(String text);
}
