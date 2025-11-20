package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class RestaurantScheduleRequest {

    @NotNull
    private UUID restaurantId;

    @NotNull
    private Integer weekday;

    @NotNull
    private LocalTime openTime;

    @NotNull
    private LocalTime closeTime;

    private Boolean special;
}
