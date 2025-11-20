package websiters.gastroreview.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddressRequest {
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
