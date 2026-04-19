package com.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    private final JdbcTemplate jdbcTemplate;
    private final UserProfileRepository userProfileRepository;
    private final ChatClient chatClient;

    public UserProfileService(JdbcTemplate jdbcTemplate, UserProfileRepository userProfileRepository,
                              ChatClient.Builder chatClientBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.userProfileRepository = userProfileRepository;
        this.chatClient = chatClientBuilder.build();
    }

    @PostConstruct
    void initializeSchema() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS USER_PROFILE (CONVERSATION_ID VARCHAR(36) NOT NULL, SUMMARY VARCHAR(16777216), UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, PRIMARY KEY (CONVERSATION_ID))");
    }

    public Optional<String> getUserProfileSummary(String conversationId) {
        return userProfileRepository.findByConversationId(conversationId).map(UserProfile::summary);
    }

    @Scheduled(fixedDelayString = "${userprofile.refresh.delay:PT5M}")
    public void refreshAllProfiles() {
        List<String> conversationIds = userProfileRepository.findAllConversationIds();
        for (String conversationId : conversationIds) {
            try {
                refreshProfile(conversationId);
            }
            catch (Exception ex) {
                // keep other profiles refreshing even if one fails
                System.err.println("Failed to refresh profile for conversation " + conversationId + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void refreshProfile(String conversationId) {
        List<String> messages = loadEarliestMessages(conversationId);
        if (messages.isEmpty()) {
            return;
        }

        String summary = summarizeProfile(messages);
        if (summary == null || summary.isBlank()) {
            return;
        }

        userProfileRepository.save(new UserProfile(conversationId, summary.trim(), LocalDateTime.now()));
    }

    private List<String> loadEarliestMessages(String conversationId) {
        return jdbcTemplate.query(
                "SELECT CONTENT FROM SPRING_AI_CHAT_MEMORY WHERE CONVERSATION_ID = ? ORDER BY TIMESTAMP ASC FETCH FIRST 10 ROWS ONLY",
                (rs, rowNum) -> rs.getString("CONTENT"),
                conversationId);
    }

    private String summarizeProfile(List<String> messages) {
        String chatHistory = String.join("\n", messages);
        String prompt = "Extract the user's interests, hobbies, and basic profile information from the following conversation history. " +
                "Return a concise summary of user tags and characteristics.\n\nConversation:\n" + chatHistory;

        Flux<String> content = chatClient.prompt()
                .system("You are an assistant that extracts user profile attributes from conversation text.")
                .user(prompt)
                .stream()
                .content();

        return content.collectList().map(list -> String.join("", list)).block();
    }
}
