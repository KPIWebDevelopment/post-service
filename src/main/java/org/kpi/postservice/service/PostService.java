package org.kpi.postservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.kpi.postservice.dto.CreatePostRequest;
import org.kpi.postservice.messaging.producer.ImageProcessingRequestProducer;
import org.kpi.postservice.model.ImageProcessingRequestMessage;
import org.kpi.postservice.model.Post;
import org.kpi.postservice.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final ImageProcessingRequestProducer imageProcessingRequestProducer;

    @Transactional
    public Post save(Post post, byte[] image) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        } else if (post.getUserId() == null || image == null) {
            throw new IllegalArgumentException("Illegal arguments provided");
        }
        post.setImageSaved(false);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        var message = new ImageProcessingRequestMessage(savedPost.getId(), image);
        imageProcessingRequestProducer.sendImageProcessingRequestMessage(message);
        return savedPost;
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
