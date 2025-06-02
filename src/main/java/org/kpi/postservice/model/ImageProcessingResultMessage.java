package org.kpi.postservice.model;

import java.util.UUID;

public record ImageProcessingResultMessage(
        UUID postId,
        ImageProcessingResultMessage imageProcessingResult
) {
}
