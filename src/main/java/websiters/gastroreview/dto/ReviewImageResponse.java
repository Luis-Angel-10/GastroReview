package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ReviewImageResponse {

    UUID id;
    UUID reviewId;
    String url;
    String altText;
}
