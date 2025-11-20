package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class DishResponse {

    UUID id;
    String name;
    String description;
    Integer priceCents;
    Boolean available;
    UUID restaurantId;
    OffsetDateTime createdAt;
}
