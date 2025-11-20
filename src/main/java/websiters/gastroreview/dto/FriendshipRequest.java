package websiters.gastroreview.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class FriendshipRequest {
    private UUID follower_Id;
    private UUID followed_Id;
}
