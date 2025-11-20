package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class RatingResponse {

    UUID id;
    UUID reviewId;
    UUID userId;
    Integer stars;
    Integer points;
    OffsetDateTime createdAt;
}
