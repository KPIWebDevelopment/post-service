package org.kpi.postservice.repository;

import org.kpi.postservice.model.Comment;
import org.kpi.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findCommentsByPostId(Post postId);
    List<Comment> findCommentsByUserId(UUID userId);
}
