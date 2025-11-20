package websiters.gastroreview.repository;

import websiters.gastroreview.model.Friendship;
import websiters.gastroreview.model.FriendshipId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {

    List<Friendship> findById_FollowerId(UUID followerId);

    List<Friendship> findById_FollowedId(UUID followedId);

    boolean existsById_FollowerIdAndId_FollowedId(UUID followerId, UUID followedId);
}
