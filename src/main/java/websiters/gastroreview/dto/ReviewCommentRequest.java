package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReviewCommentRequest {

    @NotNull
    private UUID reviewId;

    @NotNull
    private UUID authorId;

    private UUID parentId;

    @NotBlank
    private String content;
}
