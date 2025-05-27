package org.kpi.postservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kpi.postservice.model.Like;
import org.kpi.postservice.model.Post;
import org.kpi.postservice.model.User;
import org.kpi.postservice.repository.LikeRepository;
import org.kpi.postservice.repository.PostRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private LikeService likeService;

    private UUID postId;
    private UUID userId;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        userId = UUID.randomUUID();
        post = new Post();
        user = new User(userId, "Test User", "test@example.com", "password", LocalDateTime.now());
    }

    @Test
    void likePost_shouldSaveLike_WhenValid() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.findByPostIdAndUserId(post, userId)).thenReturn(null);

        likeService.likePost(postId, user.getEmail());

        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void likePost_shouldThrow_WhenUserAlreadyLiked() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.findByPostIdAndUserId(post, userId)).thenReturn(new Like());

        assertThrows(IllegalStateException.class, () ->
                likeService.likePost(postId, user.getEmail()));
    }

    @Test
    void likePost_shouldThrow_whenPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                likeService.likePost(postId, user.getEmail()));
    }

    @Test
    void deleteLike_shouldDelete_whenLikeExists() {
        UUID likeId = UUID.randomUUID();
        Like like = new Like(likeId, post, userId, LocalDateTime.now());
        when(likeRepository.findById(likeId)).thenReturn(Optional.of(like));

        likeService.deleteLike(likeId, userId);

        verify(likeRepository, times(1)).deleteById(likeId);
    }

    @Test
    void deleteLike_shouldThrowException_whenLikeNotFound() {
        UUID likeId = UUID.randomUUID();
        when(likeRepository.findById(likeId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                likeService.deleteLike(likeId, userId));
    }

    @Test
    void getAllLikesOnPost_shouldReturnLikes_whenPostExists() {
        List<Like> likes = List.of(new Like());
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findLikesByPostId(post)).thenReturn(likes);

        List<Like> result = likeService.getAllLikesOnPost(postId);

        assertEquals(likes, result);
    }

    @Test
    void getAllLikesOnPost_shouldThrowException_whenPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                likeService.getAllLikesOnPost(postId));
    }

    @Test
    void getAllLikesByUser_shouldReturnLikes() {
        List<Like> likes = List.of(new Like());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(likeRepository.findLikesByUserId(userId)).thenReturn(likes);

        List<Like> result = likeService.getAllLikesByUser(user.getEmail());

        assertEquals(likes, result);
    }
}
