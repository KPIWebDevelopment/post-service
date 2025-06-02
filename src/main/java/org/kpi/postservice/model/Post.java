package org.kpi.postservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "image_saved", nullable = false)
    boolean imageSaved = false;

    public Post(UUID userId, String text) {
        this.userId = userId;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }
}
