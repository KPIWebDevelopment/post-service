package org.kpi.postservice.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kpi.postservice.model.Post;
import org.kpi.postservice.repository.PostRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private static final UUID USER_ID = UUID.fromString("a756183c-46b3-445f-aff9-9ca4d65cb2e5");

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    @Test
    public void testSavePostSuccessfully(){
        Post post = new Post(UUID.randomUUID(), "comment to post", "htttps://localhost:8080/some-image-url");
        post.setId(UUID.randomUUID());
        when(postRepository.save(post)).thenReturn(post);

        Post savedPost = postService.save(post);

        assertThat(savedPost).isEqualTo(post);
        verify(postRepository, times(1)).save(post);
        postService.save(post);
    }

    @Test
    public void testSavePostWithNullPost(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.save(null)
        );

        assertThat(exception.getMessage()).isEqualTo("Post cannot be null");
        verifyNoInteractions(postRepository);
    }

    @Test
    public void testSavePostWithEmptyImageUrl(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.save(new Post(UUID.randomUUID(), "some text", null))
        );

        assertThat(exception.getMessage()).isEqualTo("Illegal arguments provided");
        verifyNoInteractions(postRepository);
    }

    @Test
    public void testUpdatePostSuccessfully() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Post existingPost = new Post(UUID.randomUUID(), "old text", "https://localhost:8080/old-image-url");
        existingPost.setId(postId);
        Post updatedPost = new Post(UUID.randomUUID(), "new text", "https://localhost:8080/new-image-url");
        Post savedPost = new Post(existingPost.getUserId(), "new text", "https://localhost:8080/new-image-url");
        savedPost.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(existingPost)).thenReturn(savedPost);

        // Act
        Post result = postService.update(postId, updatedPost);

        // Assert
        assertThat(result).isEqualTo(savedPost);
        assertThat(result.getText()).isEqualTo("new text");
        assertThat(result.getImageUrl()).isEqualTo("https://localhost:8080/new-image-url");
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(existingPost);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testUpdateWithNullPostThrowsException() {
        // Arrange
        UUID postId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.update(postId, null)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Post cannot be null");
        verifyNoInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testUpdateWithNonExistentPostThrowsException() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Post updatedPost = new Post(UUID.randomUUID(), "new text", "https://localhost:8080/new-image-url");
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.update(postId, updatedPost)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Post not found with id: " + postId);
        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testDeletePostSuccessfully() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Post post = new Post(UUID.randomUUID(), "text", "https://localhost:8080/image-url");
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act
        postService.delete(post);

        // Assert
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(post);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testDeleteWithNullPostThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.delete(null)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Post cannot be null");
        verifyNoInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testDeleteWithNonExistentPostThrowsException() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Post post = new Post(UUID.randomUUID(), "text", "https://localhost:8080/image-url");
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.delete(post)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Post not found with id: " + postId);
        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testGetPostByIdSuccessfully() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Post post = new Post(UUID.randomUUID(), "text", "https://localhost:8080/image-url");
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act
        Post result = postService.getPostById(postId);

        // Assert
        assertThat(result).isEqualTo(post);
        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testGetPostByIdWithNonExistentPostThrowsException() {
        // Arrange
        UUID postId = UUID.randomUUID();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.getPostById(postId)
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Post not found with id: " + postId);
        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testGetAllPostsSuccessfully() {
        // Arrange
        List<Post> posts = Arrays.asList(
                new Post(UUID.randomUUID(), "text1", "https://localhost:8080/image1"),
                new Post(UUID.randomUUID(), "text2", "https://localhost:8080/image2")
        );
        when(postRepository.findAll()).thenReturn(posts);

        // Act
        List<Post> result = postService.getAllPosts();

        // Assert
        assertEquals(result.size(), posts.size());
        verify(postRepository, times(1)).findAll();
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }

    @Test
    public void testGetAllPostsWhenEmptyThrowsException() {
        // Arrange
        when(postRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.getAllPosts()
        );

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Posts not found");
        verify(postRepository, times(1)).findAll();
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(userService);
    }
}

