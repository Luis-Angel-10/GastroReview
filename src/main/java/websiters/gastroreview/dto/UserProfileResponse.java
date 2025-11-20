package websiters.gastroreview.dto;


import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class UserProfileResponse {
    UUID user_Id;
    String photo_Url;
    String name;
    boolean active;
    OffsetDateTime createdAt;
    String bio;
}