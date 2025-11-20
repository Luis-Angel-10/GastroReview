package websiters.gastroreview.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false) private String street;
    private String site;
    private String neighborhood;
    private String city;
    private String stateRegion;
    private Integer postalCode;
    @Column(nullable = false) private String country = "MX";
    private Double latitude;
    private Double longitude;



    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RestaurantAddress> restaurantLinks = new ArrayList<>();
}
