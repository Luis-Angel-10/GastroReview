package websiters.gastroreview.dto;


import lombok.Data;

import java.util.UUID;

@Data

public class UserProfileRequest {
    UUID user_Id;
    String photo_Url;
    String name;
    Boolean active;
    String bio;
}