package websiters.gastroreview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class FavoriteReviewId implements Serializable {
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "review_id")
    private UUID reviewId;
}
