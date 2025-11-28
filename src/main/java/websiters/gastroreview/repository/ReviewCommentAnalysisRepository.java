package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import websiters.gastroreview.model.ReviewCommentAnalysis;

import java.util.Optional;
import java.util.UUID;

public interface ReviewCommentAnalysisRepository extends JpaRepository<ReviewCommentAnalysis, UUID> {
    Optional<ReviewCommentAnalysis> findByComment_Id(UUID commentId);
}
