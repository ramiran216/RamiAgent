package com.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class HelloController {
    
    private final ChatClient chatClient;

    @Autowired(required = true)
    private JdbcChatMemoryRepository chatMemoryRepository;
    
    public HelloController(ChatClient.Builder chatClientBuilder) {

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
    
    @GetMapping(value = "/deepseek", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> deepseek(
            @RequestParam(defaultValue = "你好，请介绍一下你自己") @NonNull String message,
            @RequestParam(defaultValue = "default-conversation") String conversationId) {
        return chatClient.prompt()
            .advisors(advisor -> advisor.param(MessageChatMemoryAdvisor.CONVERSATION_ID_KEY, conversationId))
            .user(message)
            .stream()
            .content();
    }
}