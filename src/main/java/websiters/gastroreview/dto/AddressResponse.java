package websiters.gastroreview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private UUID id;
    private String street;
    private String site;
    private String neighborhood;
    private String city;
    private String state_region;
    private Integer postal_code;
    private String country;
    private Double latitude;
    private Double longitude;
}
