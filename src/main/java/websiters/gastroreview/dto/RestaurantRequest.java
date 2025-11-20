package websiters.gastroreview.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data

public class RestaurantRequest{
        @NotBlank String name;
        String description;
        String phone;
        String email;
        UUID owner_Id;
}
