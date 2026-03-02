package com.poc.chat.service;

import com.poc.chat.BaseUnitTest;
import com.poc.chat.domain.ChatConversation;
import com.poc.chat.domain.ChatMessage;
import com.poc.chat.domain.ChatUser;
import com.poc.chat.dto.chat.ChatConversationDTO;
import com.poc.chat.dto.chat.CreateConversationRequest;
import com.poc.chat.dto.chat.SendMessageRequest;
import com.poc.chat.repository.ChatConversationRepository;
import com.poc.chat.repository.ChatMessageRepository;
import com.poc.chat.repository.ChatUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ChatServiceTest extends BaseUnitTest {

    @Mock
    private ChatUserRepository chatUserRepository;

    @Mock
    private ChatConversationRepository chatConversationRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void getConversations_shouldReturnEmptyList_whenUserNotFound() {
        when(chatUserRepository.findByExternalUserIdAndTenantId(TEST_USER_ID, TEST_TENANT_ID))
                .thenReturn(Optional.empty());

        List<ChatConversationDTO> result = chatService.getConversations();

        assertThat(result).isEmpty();
    }

    @Test
    void getConversations_shouldReturnConversations_whenUserExists() {
        ChatUser currentUser = buildChatUser(1L, TEST_USER_ID);
        ChatUser otherUser = buildChatUser(2L, java.util.UUID.randomUUID());
        ChatConversation conversation = buildConversation(1L, 1L, 2L);

        when(chatUserRepository.findByExternalUserIdAndTenantId(TEST_USER_ID, TEST_TENANT_ID))
                .thenReturn(Optional.of(currentUser));
        when(chatConversationRepository.findByUserIdOrderByLastMessageAtDesc(TEST_TENANT_ID, 1L))
                .thenReturn(List.of(conversation));
        when(chatUserRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(chatMessageRepository.findLastMessage(1L)).thenReturn(null);
        when(chatMessageRepository.countUnreadMessages(1L, 1L)).thenReturn(0);

        List<ChatConversationDTO> result = chatService.getConversations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOtherParticipant().getId()).isEqualTo(2L);
    }

    @Test
    void createOrGetConversation_shouldThrow_whenOtherUserNotFound() {
        ChatUser currentUser = buildChatUser(1L, TEST_USER_ID);
        when(chatUserRepository.findByExternalUserIdAndTenantId(TEST_USER_ID, TEST_TENANT_ID))
                .thenReturn(Optional.of(currentUser));
        when(chatUserRepository.findById(99L)).thenReturn(Optional.empty());

        CreateConversationRequest request = CreateConversationRequest.builder().otherUserId(99L).build();

        assertThatThrownBy(() -> chatService.createOrGetConversation(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Other user not found");
    }

    @Test
    void sendMessage_shouldCreateAndReturnMessage() {
        ChatUser currentUser = buildChatUser(1L, TEST_USER_ID);
        ChatConversation conversation = buildConversation(1L, 1L, 2L);

        when(chatUserRepository.findByExternalUserIdAndTenantId(TEST_USER_ID, TEST_TENANT_ID))
                .thenReturn(Optional.of(currentUser));
        when(chatConversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage msg = invocation.getArgument(0);
            msg.setId(100L);
            return msg;
        });

        SendMessageRequest request = SendMessageRequest.builder()
                .conversationId(1L)
                .text("Hello!")
                .build();

        var result = chatService.sendMessage(request);

        assertThat(result.getText()).isEqualTo("Hello!");
        assertThat(result.isOwnMessage()).isTrue();
        verify(chatConversationRepository).updateLastMessageAt(eq(1L), any(Instant.class), any(Instant.class));
    }

    private ChatUser buildChatUser(Long id, java.util.UUID externalUserId) {
        return ChatUser.builder()
                .id(id)
                .externalUserId(externalUserId)
                .tenantId(TEST_TENANT_ID)
                .name("Test User " + id)
                .email("user" + id + "@test.com")
                .status("ONLINE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private ChatConversation buildConversation(Long id, Long participantOneId, Long participantTwoId) {
        return ChatConversation.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .participantOneId(participantOneId)
                .participantTwoId(participantTwoId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
