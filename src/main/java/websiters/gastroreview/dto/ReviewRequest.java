package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReviewRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID restaurantId;

    private UUID dishId;

    private String title;

    private String content;

    private Boolean hasAudio;

    private Boolean hasImage;
}
