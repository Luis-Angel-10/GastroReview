package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class FavoriteRestaurantResponse {

    UUID userId;
    UUID restaurantId;
    OffsetDateTime createdAt;
}
