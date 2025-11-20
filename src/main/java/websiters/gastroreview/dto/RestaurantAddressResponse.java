package websiters.gastroreview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class RestaurantAddressResponse {
    private UUID restaurant_Id;
    private UUID address_Id;
    private boolean is_primary;
    private String branch_name;
}
