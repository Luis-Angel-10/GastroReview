package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class RestaurantImageResponse {

    UUID id;
    UUID restaurantId;
    String url;
    String altText;
    UUID uploadedBy;
    OffsetDateTime createdAt;
}
