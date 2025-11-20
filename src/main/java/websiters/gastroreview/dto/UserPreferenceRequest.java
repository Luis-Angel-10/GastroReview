package websiters.gastroreview.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class UserPreferenceRequest {

    private UUID user_Id;

    @JsonProperty("key")
    private String pref_key;

    private Map<String, Object> value;
}