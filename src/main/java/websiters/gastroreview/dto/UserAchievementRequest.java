package websiters.gastroreview.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class UserAchievementRequest {
    private UUID user_Id;
    private String badge;
    private Integer level;
    private Integer stars;
}
