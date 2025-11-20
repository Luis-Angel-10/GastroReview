package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AlertRequest {

    @NotBlank
    private String type;

    private UUID restaurantId;

    private UUID reviewId;

    private String detail;
}
