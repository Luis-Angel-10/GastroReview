package websiters.gastroreview.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class UserRoleRequest {
    @NotNull
    private UUID user_Id;

    @NotNull
    private UUID role_Id;

}

