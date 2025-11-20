package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ReviewAudioResponse {

    UUID id;
    UUID reviewId;
    String url;
    Integer durationSeconds;
    String transcription;
}
