package org.kpi.postservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.kpi.postservice.model.Comment;
import org.kpi.postservice.model.Post;
import org.kpi.postservice.repository.CommentRepository;
import org.kpi.postservice.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.kpi.postservice.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Transactional
    public void commentPost(UUID postId, String userEmail, String commentText) {
        if (postId == null || userEmail == null) {
            throw new IllegalArgumentException("PostId or UserId cannot be null");
        }
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        User user = userService.getUserByEmail(userEmail);

        Comment comment = new Comment(post, user.getId(), commentText, LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(UUID commentId, UUID currentUserId) {
        if (commentId == null || currentUserId == null) {
            throw new IllegalArgumentException("CommentId or UserId cannot be null");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getAllCommentsOnPost(UUID postId) {
        if (postId == null) {
            throw new IllegalArgumentException("PostId cannot be null");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        return commentRepository.findCommentsByPostId(post);
    }

    public List<Comment> getAllCommentsByUser(String userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        User user = userService.getUserByEmail(userEmail);
        return commentRepository.findCommentsByUserId(user.getId());
    }
}