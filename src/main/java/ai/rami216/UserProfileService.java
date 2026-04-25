package ai.rami216;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final JdbcTemplate jdbcTemplate;
    private final UserProfileRepository userProfileRepository;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatClient chatClient;

    public UserProfileService(JdbcTemplate jdbcTemplate, UserProfileRepository userProfileRepository,
                              ChatMemoryRepository chatMemoryRepository, ChatClient.Builder chatClientBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.userProfileRepository = userProfileRepository;
        this.chatMemoryRepository = chatMemoryRepository;
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
        List<String> conversationIds = discoverConversationIds();
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

    private List<String> discoverConversationIds() {
        return chatMemoryRepository.findConversationIds();
    }

    public void refreshProfile(String conversationId) {
        List<String> messages = loadEarliestMessages(conversationId);
        if (messages.isEmpty()) {
            return;
        }

        List<String> promptMessages = preparePromptMessages(conversationId, messages);
        String summary = summarizeProfile(promptMessages);
        if (summary == null || summary.isBlank()) {
            return;
        }

        userProfileRepository.save(new UserProfile(conversationId, summary.trim(), LocalDateTime.now()));
    }

    private List<String> preparePromptMessages(String conversationId, List<String> messages) {
        List<String> promptMessages = new ArrayList<>(messages);
        getUserProfileSummary(conversationId).ifPresent(promptMessages::add);
        return promptMessages;
    }

    private List<String> loadEarliestMessages(String conversationId) {
        return chatMemoryRepository.findByConversationId(conversationId).stream()
                .limit(10)
                .map(org.springframework.ai.chat.messages.Message::getText)
                .collect(Collectors.toList());
    }

    private String summarizeProfile(List<String> promptMessages) {
        String chatHistory = String.join("\n", promptMessages);
        String prompt = "Extract the user's names, interests, hobbies, and basic profile information from the following conversation history. " +
                "Return a concise summary of user tags and characteristics.\n\nConversation:\n" + chatHistory;

        Flux<String> content = chatClient.prompt()
                .system("You are an assistant that extracts user profile attributes from conversation text.")
                .user(prompt)
                .stream()
                .content();

        return content.collectList().map(list -> String.join("", list)).block();
    }
}
