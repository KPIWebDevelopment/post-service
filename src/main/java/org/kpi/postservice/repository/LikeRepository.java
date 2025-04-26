package org.kpi.postservice.repository;

import org.kpi.postservice.model.Like;
import org.kpi.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    List<Like> findLikesByPostId(Post postId);
    Like findByPostIdAndUserId(Post postId, UUID userId);
    List<Like> findLikesByUserId(UUID userId);
}
