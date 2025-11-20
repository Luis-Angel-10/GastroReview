package websiters.gastroreview.dto;

import lombok.Value;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class FavoriteReviewResponse {
    UUID userId;
    UUID reviewId;
    OffsetDateTime createdAt;
}
