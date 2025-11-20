package websiters.gastroreview.model;

import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FavoriteRestaurantId implements Serializable {

    @JdbcTypeCode(SqlTypes.UUID)
    private UUID userId;

    @JdbcTypeCode(SqlTypes.UUID)
    private UUID restaurantId;
}
