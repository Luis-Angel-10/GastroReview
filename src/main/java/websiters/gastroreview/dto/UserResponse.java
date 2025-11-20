package websiters.gastroreview.dto;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Value
@Builder

public class UserResponse{
        UUID id;
        String email;
        Set<String> roles;
}
