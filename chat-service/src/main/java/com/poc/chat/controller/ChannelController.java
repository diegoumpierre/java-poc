package com.poc.chat.controller;

import com.poc.chat.dto.chat.*;
import com.poc.chat.service.AttachmentService;
import com.poc.chat.service.ChannelService;
import com.poc.chat.service.ChatService;
import com.poc.chat.service.ReactionService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final ChatService chatService;
    private final ReactionService reactionService;
    private final AttachmentService attachmentService;

    @GetMapping
    public ResponseEntity<List<ChatChannelDTO>> getUserChannels() {
        return ResponseEntity.ok(channelService.getUserChannels());
    }

    @PostMapping
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatChannelDTO> createChannel(@Valid @RequestBody CreateChannelRequest request) {
        return ResponseEntity.ok(channelService.createChannel(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatChannelDTO> getChannel(@PathVariable Long id) {
        return channelService.getChannel(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatChannelDTO> updateChannel(@PathVariable Long id, @RequestBody UpdateChannelRequest request) {
        return ResponseEntity.ok(channelService.updateChannel(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> archiveChannel(@PathVariable Long id) {
        channelService.archiveChannel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public")
    public ResponseEntity<List<ChatChannelDTO>> getPublicChannels() {
        return ResponseEntity.ok(channelService.getPublicChannels());
    }

    @PostMapping("/{id}/join")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> joinChannel(@PathVariable Long id) {
        channelService.joinChannel(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> leaveChannel(@PathVariable Long id) {
        channelService.leaveChannel(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ChannelMemberDTO>> getMembers(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getMembers(id));
    }

    @PostMapping("/{id}/members")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> addMembers(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> userIds = body.get("userIds");
        channelService.addMembers(id, userIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        channelService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.getChannelMessages(id, offset, limit));
    }

    @PostMapping("/{id}/messages")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @PathVariable Long id,
            @RequestBody SendMessageRequest request) {
        request.setChannelId(id);
        return ResponseEntity.ok(chatService.sendChannelMessage(request));
    }

    @PutMapping("/{id}/read")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<Void> updateReadCursor(@PathVariable Long id) {
        channelService.updateReadCursor(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dm")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatChannelDTO> createOrGetDm(@RequestBody Map<String, Long> body) {
        Long otherUserId = body.get("otherUserId");
        return ResponseEntity.ok(channelService.createOrGetDm(otherUserId));
    }

    // Thread endpoints
    @GetMapping("/{id}/messages/{msgId}/thread")
    public ResponseEntity<List<ChatMessageDTO>> getThreadMessages(
            @PathVariable Long id,
            @PathVariable Long msgId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.getThreadMessages(msgId, offset, limit));
    }

    // Reaction endpoints
    @PostMapping("/messages/{msgId}/reactions")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<List<ChatReactionDTO>> addReaction(
            @PathVariable Long msgId,
            @Valid @RequestBody ReactionRequest request) {
        return ResponseEntity.ok(reactionService.addReaction(msgId, request.getEmoji()));
    }

    @DeleteMapping("/messages/{msgId}/reactions/{emoji}")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<List<ChatReactionDTO>> removeReaction(
            @PathVariable Long msgId,
            @PathVariable String emoji) {
        return ResponseEntity.ok(reactionService.removeReaction(msgId, emoji));
    }

    // Edit message
    @PutMapping("/messages/{msgId}")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatMessageDTO> editMessage(
            @PathVariable Long msgId,
            @Valid @RequestBody EditMessageRequest request) {
        return ResponseEntity.ok(chatService.editMessage(msgId, request.getText()));
    }

    // Attachment endpoints
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<ChatAttachmentDTO>> getChannelAttachments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(attachmentService.getChannelAttachments(id, offset, limit));
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        Path filePath = attachmentService.getAttachmentFile(attachmentId);
        String mimeType = attachmentService.getAttachmentMimeType(attachmentId);
        Resource resource = new PathResource(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @PostMapping("/{id}/messages/with-files")
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatMessageDTO> sendMessageWithFiles(
            @PathVariable Long id,
            @RequestParam("text") String text,
            @RequestParam(value = "parentMessageId", required = false) Long parentMessageId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {

        SendMessageRequest request = new SendMessageRequest();
        request.setChannelId(id);
        request.setText(text);
        request.setParentMessageId(parentMessageId);

        ChatMessageDTO messageDTO = chatService.sendChannelMessage(request);

        // Save attachments
        for (MultipartFile file : files) {
            attachmentService.saveAttachment(messageDTO.getId(), file);
        }

        return ResponseEntity.ok(messageDTO);
    }
}
