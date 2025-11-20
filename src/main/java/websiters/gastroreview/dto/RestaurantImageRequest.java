package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RestaurantImageRequest {

    @NotNull
    private UUID restaurantId;

    @NotBlank
    private String url;

    private String altText;

    private UUID uploadedBy;
}
