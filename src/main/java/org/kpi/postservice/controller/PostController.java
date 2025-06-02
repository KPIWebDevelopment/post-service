package org.kpi.postservice.controller;

import org.kpi.postservice.dto.CreatePostRequest;
import org.kpi.postservice.dto.CreatePostResponse;
import org.kpi.postservice.model.Comment;
import org.kpi.postservice.model.Post;
import org.kpi.postservice.model.User;
import org.kpi.postservice.service.CommentService;
import org.kpi.postservice.service.LikeService;
import org.kpi.postservice.service.PostService;
import org.kpi.postservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final LikeService likeService;
    private final CommentService commentService;

    public PostController(PostService postService, UserService userService, LikeService likeService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.likeService = likeService;
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CreatePostResponse> createPost(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestPart("post") CreatePostRequest post,
            @RequestPart("image") MultipartFile image
    ) {
        try {
            byte[] imageBytes = image.getBytes();
            Post postToSave = new Post(post.userId(), post.text());
            Post savedPost = postService.save(postToSave, imageBytes);
            CreatePostResponse response = new CreatePostResponse(parseTokenToEmail(authorizationHeader), savedPost.getText());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/posts-by-user")
    public ResponseEntity<List<Post>> getPostsByUser(@RequestHeader("Authorization") String authorizationHeader) {
        List<Post> posts = postService.getPostsByUser(parseTokenToEmail(authorizationHeader));
        return ResponseEntity.ok(posts);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable UUID postId) {
        User user = userService.getUserByEmail(parseTokenToEmail(authorizationHeader));
        likeService.likePost(postId, user.getEmail());
        return ResponseEntity.ok("Post liked successfully");
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UUID postId, @RequestBody String text) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post does not exist");
        }
        Comment comment = new Comment(text);
        commentService.commentPost(postId, parseTokenToEmail(authorizationHeader), text);
        return ResponseEntity.ok(comment);
    }

    private String parseTokenToEmail(String authorization) {
        String jwtToken = authorization.substring(7);
        return userService.getUsernameFromToken(jwtToken);
    }
}

