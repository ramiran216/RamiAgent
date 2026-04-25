package ai.rami216;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {

    private JdbcTemplate jdbcTemplate;
    private UserProfileRepository userProfileRepository;
    private ChatMemoryRepository chatMemoryRepository;
    private ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;
    private ChatClient.ChatClientRequestSpec requestSpec;
    private ChatClient.StreamResponseSpec streamSpec;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        userProfileRepository = mock(UserProfileRepository.class);
        chatMemoryRepository = mock(ChatMemoryRepository.class);
        chatClientBuilder = mock(ChatClient.Builder.class);
        chatClient = mock(ChatClient.class);
        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        streamSpec = mock(ChatClient.StreamResponseSpec.class);

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.stream()).thenReturn(streamSpec);
    }

    @Test
    void shouldBootstrapProfileWhenUserProfileTableIsEmptyButChatMemoryContainsConversations() {
        String conversationId = "conversation-1";
        Message firstMessage = mock(Message.class);
        Message secondMessage = mock(Message.class);

        when(chatMemoryRepository.findConversationIds()).thenReturn(List.of(conversationId));
        when(chatMemoryRepository.findByConversationId(conversationId)).thenReturn(List.of(firstMessage, secondMessage));
        when(firstMessage.getText()).thenReturn("hello");
        when(secondMessage.getText()).thenReturn("world");
        when(userProfileRepository.findByConversationId(conversationId)).thenReturn(Optional.empty());
        when(streamSpec.content()).thenReturn(Flux.just("summary"));

        UserProfileService service = new UserProfileService(jdbcTemplate, userProfileRepository, chatMemoryRepository, chatClientBuilder);
        service.refreshAllProfiles();

        ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileRepository).save(profileCaptor.capture());
        UserProfile savedProfile = profileCaptor.getValue();

        assertEquals(conversationId, savedProfile.conversationId());
        assertEquals("summary", savedProfile.summary());
        assertTrue(savedProfile.updatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldNoOpWhenChatMemoryIsEmpty() {
        String conversationId = "conversation-1";
        when(chatMemoryRepository.findConversationIds()).thenReturn(List.of(conversationId));
        when(chatMemoryRepository.findByConversationId(conversationId)).thenReturn(List.of());

        UserProfileService service = new UserProfileService(jdbcTemplate, userProfileRepository, chatMemoryRepository, chatClientBuilder);
        service.refreshAllProfiles();

        verify(chatMemoryRepository).findConversationIds();
        verify(userProfileRepository, org.mockito.Mockito.never()).save(any());
    }
}
