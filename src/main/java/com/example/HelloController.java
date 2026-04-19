package com.example;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class HelloController {
    
    private final ChatClient chatClient;
    
    public HelloController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    
    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot!";
    }
    
    @GetMapping(value = "/deepseek", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> deepseek(@RequestParam(defaultValue = "你好，请介绍一下你自己") @NonNull String message) {
        return chatClient.prompt()
            .user(message)
            .stream()
            .content();
    }
}