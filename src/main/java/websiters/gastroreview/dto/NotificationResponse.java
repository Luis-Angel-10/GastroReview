package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class NotificationResponse {

    UUID id;
    UUID userId;
    String type;
    String message;
    boolean read;
    UUID referenceId;
    Map<String, Object> metadata;
}
