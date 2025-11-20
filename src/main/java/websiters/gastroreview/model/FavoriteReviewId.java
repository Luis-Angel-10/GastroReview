package websiters.gastroreview.model;

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
    private UUID userId;
    private UUID reviewId;
}
