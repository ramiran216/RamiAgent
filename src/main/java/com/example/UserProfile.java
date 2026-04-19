package com.example;

import java.time.LocalDateTime;

public record UserProfile(String conversationId, String summary, LocalDateTime updatedAt) {
}
