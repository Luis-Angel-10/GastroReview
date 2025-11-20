package websiters.gastroreview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class UserRoleResponse {
    private UUID user_Id;
    private UUID role_Id;
    private OffsetDateTime assignedAt;
}
