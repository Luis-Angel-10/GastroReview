package websiters.gastroreview.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FriendshipId implements Serializable {
    private UUID followerId;
    private UUID followedId;
}

