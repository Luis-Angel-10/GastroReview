package websiters.gastroreview.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UserRequest{

        @Email
        @NotBlank String email;
        @NotBlank String password;
        @NotBlank String role;

}