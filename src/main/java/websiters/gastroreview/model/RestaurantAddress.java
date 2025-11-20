package websiters.gastroreview.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_address")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantAddress {

    @EmbeddedId
    private RestaurantAddressId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("restaurantId")
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("addressId")
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false)
    private boolean isPrimary = false;

    private String branchName;
}
