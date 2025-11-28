package websiters.gastroreview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_comment_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentAnalysis {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false, unique = true)
    private ReviewComment comment;

    private String sentiment;

    private Double positiveScore;
    private Double neutralScore;
    private Double negativeScore;

    @Column(columnDefinition = "TEXT")
    private String keyPhrases;

    private OffsetDateTime analyzedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (analyzedAt == null) analyzedAt = OffsetDateTime.now();
    }
}
