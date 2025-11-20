package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReviewImageRequest {

    @NotNull
    private UUID reviewId;

    @NotBlank
    private String url;

    private String altText;
}
