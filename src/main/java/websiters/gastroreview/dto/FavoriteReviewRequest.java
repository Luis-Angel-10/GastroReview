package websiters.gastroreview.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class FavoriteReviewRequest {
    @NotNull
    private UUID userId;

    @NotNull
    private UUID reviewId;
}
