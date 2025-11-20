package websiters.gastroreview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserPreferenceResponse {

    private UUID id;

    @JsonProperty("key")
    private String pref_key;

    private Map<String, Object> value;
}
