package websiters.gastroreview.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class RestaurantAddressRequest {
    private UUID restaurant_Id;
    private UUID address_Id;
    private Boolean is_primary;
    private String branch_name;
}
