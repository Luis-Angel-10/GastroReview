package websiters.gastroreview.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public class ReviewResponse {
    UUID id;
    UUID userId;
    UUID restaurantId;
    String title;
    String content;
    boolean hasAudio;
    boolean hasImage;
    OffsetDateTime publishedAt;
}
