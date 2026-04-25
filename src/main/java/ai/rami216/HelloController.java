package ai.rami216;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;

import java.util.Optional;

@RestController
public class HelloController {
    
    private ChatClient chatClient;

    @Autowired
    private JdbcChatMemoryRepository chatMemoryRepository;
    
    private final ChatClient.Builder chatClientBuilder;
    private final UserProfileService userProfileService;

    public HelloController(ChatClient.Builder chatClientBuilder, UserProfileService userProfileService) {
        this.chatClientBuilder = chatClientBuilder;
        this.userProfileService = userProfileService;
    }

    @SuppressWarnings("null")
    @PostConstruct
    private void init() {
        
        Assert.notNull(chatMemoryRepository, "chatMemoryRepository must not be null");
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(5)
            .build();

        this.chatClient = chatClientBuilder
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();
    }
    
    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot!";
    }
    
    @SuppressWarnings("null")
    @GetMapping(value = "/deepseek", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> deepseek(
            @RequestParam(defaultValue = "你好，请介绍一下你自己") @NonNull String message,
            @RequestParam(defaultValue = "qianqian") String conversationId) {
        var prompt = chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId));

        Optional<String> profileSummary = userProfileService.getUserProfileSummary(conversationId);
        profileSummary.ifPresent(prompt::system);

        return prompt.user(message)
                .stream()
                .content();
    }
}