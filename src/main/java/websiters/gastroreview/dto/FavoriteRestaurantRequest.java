package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FavoriteRestaurantRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID restaurantId;
}
