package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class ReviewCommentResponse {

    UUID id;
    UUID reviewId;
    UUID authorId;
    UUID parentId;
    String content;
    OffsetDateTime publishedAt;
}
