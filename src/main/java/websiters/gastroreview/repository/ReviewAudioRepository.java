package websiters.gastroreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import websiters.gastroreview.model.ReviewAudio;

import java.util.List;
import java.util.UUID;

public interface ReviewAudioRepository extends JpaRepository<ReviewAudio, UUID> {

    List<ReviewAudio> findByReview_Id(UUID reviewId);
}
