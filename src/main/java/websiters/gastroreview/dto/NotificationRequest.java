package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class NotificationRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String type;

    @NotBlank
    private String message;

    private UUID referenceId;

    private Map<String, Object> metadata;
}
