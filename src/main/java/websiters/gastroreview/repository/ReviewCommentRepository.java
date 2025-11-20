package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import websiters.gastroreview.model.ReviewComment;

import java.util.List;
import java.util.UUID;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, UUID> {

    List<ReviewComment> findByReview_Id(UUID reviewId);

    List<ReviewComment> findByParent_Id(UUID parentId);

    List<ReviewComment> findByAuthor_Id(UUID authorId);
}
