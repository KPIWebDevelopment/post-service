package org.kpi.postservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.kpi.postservice.model.Like;
import org.kpi.postservice.model.Post;
import org.kpi.postservice.model.User;
import org.kpi.postservice.repository.LikeRepository;
import org.kpi.postservice.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserService userService) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Transactional
    public void likePost(UUID postId, String userEmail) {
        if (postId == null || userEmail == null) {
            throw new IllegalArgumentException("PostId or UserId cannot be null");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        User user = userService.getUserByEmail(userEmail);

        if (likeRepository.findByPostIdAndUserId(post, user.getId()) != null) {
            throw new IllegalStateException("User already liked this post");
        }

        Like like = new Like(post, user.getId(), LocalDateTime.now());
        likeRepository.save(like);
    }

    @Transactional
    public void deleteLike(UUID likeId, UUID currentUserId) {
        if (likeId == null || currentUserId == null) {
            throw new IllegalArgumentException("LikeId or UserId cannot be null");
        }
        Like like = likeRepository.findById(likeId)
                .orElse(null);

        if (like == null) {
            throw new IllegalStateException("Like not found with id: " + likeId);
        }
        likeRepository.deleteById(likeId);
    }

    public List<Like> getAllLikesOnPost(UUID postId) {
        if (postId == null) {
            throw new IllegalArgumentException("PostId cannot be null");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        return likeRepository.findLikesByPostId(post);
    }

    public List<Like> getAllLikesByUser(String userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        User user = userService.getUserByEmail(userEmail);
        return likeRepository.findLikesByUserId(user.getId());
    }
}