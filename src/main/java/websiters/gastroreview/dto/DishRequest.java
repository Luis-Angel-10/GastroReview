package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DishRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Integer priceCents;

    private Boolean available;

    @NotNull
    private UUID restaurantId;
}
