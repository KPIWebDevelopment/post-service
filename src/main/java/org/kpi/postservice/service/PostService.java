package org.kpi.postservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.kpi.postservice.dto.CreatePostRequest;
import org.kpi.postservice.model.Post;
import org.kpi.postservice.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Transactional
    public Post save(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        } else if (post.getUserId() == null || post.getImageUrl() == null) {
            throw new IllegalArgumentException("Illegal arguments provided");
        }

        return postRepository.save(post);
    }

    public List<Post> getPostsByUser(String userEmail) {
        return postRepository.findPostsByUserId(userService.getUserByEmail(userEmail).getId());
    }

    @Transactional
    public Post update(UUID postId, Post updatedPost) {
        if (updatedPost == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

        existingPost.setText(updatedPost.getText());
        existingPost.setImageUrl(updatedPost.getImageUrl());
        return postRepository.save(existingPost);
    }

    @Transactional
    public void delete(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }
        Post postToDelete = postRepository.findById(post.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + post.getId()));
        postRepository.delete(postToDelete);
    }

    public Post getPostById(UUID postId) {
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
    }

    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        if (posts.isEmpty()) {
            throw new EntityNotFoundException("Posts not found");
        }
        return posts;
    }
}
