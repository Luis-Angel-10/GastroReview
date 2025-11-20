package websiters.gastroreview.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantAddressId implements Serializable {
    private UUID restaurantId;
    private UUID addressId;
}

