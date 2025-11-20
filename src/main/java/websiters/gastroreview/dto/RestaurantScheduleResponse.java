package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class RestaurantScheduleResponse {

    UUID id;
    UUID restaurantId;
    Integer weekday;
    LocalTime openTime;
    LocalTime closeTime;
    Boolean special;
    OffsetDateTime createdAt;
}
