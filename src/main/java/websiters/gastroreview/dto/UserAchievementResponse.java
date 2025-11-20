package websiters.gastroreview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class UserAchievementResponse {
    private UUID id;
    private String badge;
    private Integer level;
    private Integer stars;
}
