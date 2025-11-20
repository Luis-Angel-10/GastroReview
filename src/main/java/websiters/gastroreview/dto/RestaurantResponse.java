package websiters.gastroreview.dto;


import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder

public class RestaurantResponse {
    UUID id;
    String name;
    String description;
    String phone;
    String email;
    UUID owner_Id;
}