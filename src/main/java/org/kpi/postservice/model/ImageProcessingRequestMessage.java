package org.kpi.postservice.model;

import java.util.UUID;

public record ImageProcessingRequestMessage(
        UUID postId,
        byte[] image
) {
}
