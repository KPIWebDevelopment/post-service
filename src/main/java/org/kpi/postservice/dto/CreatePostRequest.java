package org.kpi.postservice.dto;

import java.util.UUID;

public record CreatePostRequest(UUID userId, String text) {
}
