package websiters.gastroreview.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_achievements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","badge"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false) private String badge;
    @Column(nullable = false) private Integer level;
    @Column(nullable = false) private Integer stars;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime obtainedAt;

    @PrePersist
    void prePersist() {
        if (level == null) level = 1;
        if (stars == null) stars = 0;
        if (obtainedAt == null) obtainedAt = OffsetDateTime.now();
        if (badge != null) badge = badge.trim();
    }
}
