package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class AlertResponse {

    UUID id;
    String type;
    UUID restaurantId;
    UUID reviewId;
    String detail;
    OffsetDateTime createdAt;
}
