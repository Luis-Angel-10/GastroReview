package websiters.gastroreview.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurant_schedules",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"restaurant_id", "weekday", "open_time", "close_time"}
        ))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantSchedule {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @NotNull
    @Column(nullable = false)
    private Integer weekday;

    @NotNull
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @NotNull
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false)
    private Boolean special = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
