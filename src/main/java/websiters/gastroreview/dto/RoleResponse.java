package websiters.gastroreview.dto;


import lombok.Builder;
import lombok.Value;


import java.util.UUID;

@Value
@Builder

public class RoleResponse {
     UUID id;
     String name;
     String description;
}
