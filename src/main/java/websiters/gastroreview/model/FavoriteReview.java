package websiters.gastroreview.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "favorite_reviews")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteReview {

    @EmbeddedId
    private FavoriteReviewId id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @Column(name = "review_id", insertable = false, updatable = false)
    private UUID reviewId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
