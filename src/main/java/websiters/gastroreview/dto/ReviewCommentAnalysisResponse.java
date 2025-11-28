package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class ReviewCommentAnalysisResponse {

    UUID id;
    UUID commentId;

    String sentiment;

    Double positiveScore;
    Double neutralScore;
    Double negativeScore;

    String keyPhrases;

    OffsetDateTime analyzedAt;
}
