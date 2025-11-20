package websiters.gastroreview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class FriendshipResponse {
    private UUID follower_Id;
    private UUID followed_Id;
    private OffsetDateTime createdAt;
}
