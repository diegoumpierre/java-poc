package com.poc.notification.controller;

import com.poc.notification.dto.MailModel;
import com.poc.notification.dto.MailPageResponse;
import com.poc.notification.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Mail", description = "Mail inbox/sent management APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
@Slf4j
public class MailController {

    private final MailService mailService;

    // ==================== Folder Endpoints ====================

    @Operation(summary = "Get inbox", description = "Get paginated inbox mails")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inbox retrieved successfully")
    })
    @GetMapping("/inbox")
    public ResponseEntity<MailPageResponse> getInbox(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getInbox(userId, page, size));
    }

    @Operation(summary = "Get starred", description = "Get paginated starred mails")
    @GetMapping("/starred")
    public ResponseEntity<MailPageResponse> getStarred(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getStarred(userId, page, size));
    }

    @Operation(summary = "Get important", description = "Get paginated important mails")
    @GetMapping("/important")
    public ResponseEntity<MailPageResponse> getImportant(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getImportant(userId, page, size));
    }

    @Operation(summary = "Get sent", description = "Get paginated sent mails")
    @GetMapping("/sent")
    public ResponseEntity<MailPageResponse> getSent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getSent(userId, page, size));
    }

    @Operation(summary = "Get trash", description = "Get paginated trash mails")
    @GetMapping("/trash")
    public ResponseEntity<MailPageResponse> getTrash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getTrash(userId, page, size));
    }

    @Operation(summary = "Get spam", description = "Get paginated spam mails")
    @GetMapping("/spam")
    public ResponseEntity<MailPageResponse> getSpam(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getSpam(userId, page, size));
    }

    @Operation(summary = "Get archived", description = "Get paginated archived mails")
    @GetMapping("/archived")
    public ResponseEntity<MailPageResponse> getArchived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getArchived(userId, page, size));
    }

    // ==================== Mail CRUD Endpoints ====================

    @Operation(summary = "Get all mails", description = "Get all mails for user (legacy compatibility)")
    @GetMapping
    public ResponseEntity<List<MailModel>> getAllMails(
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getAllMails(userId));
    }

    @Operation(summary = "Get mail by ID", description = "Get a specific mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mail retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Mail not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MailModel> getMailById(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getById(id, userId));
    }

    @Operation(summary = "Search mails", description = "Search mails by query")
    @GetMapping("/search")
    public ResponseEntity<List<MailModel>> searchMails(
            @Parameter(description = "Search query") @RequestParam("q") String query,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.search(userId, query));
    }

    @Operation(summary = "Get mail counts", description = "Get folder counts for sidebar badges")
    @GetMapping("/counts")
    public ResponseEntity<MailService.MailCounts> getCounts(
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.getCounts(userId));
    }

    @Operation(summary = "Send/Create mail", description = "Create a new mail (sent)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mail created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<MailModel> createMail(
            @Valid @RequestBody CreateMailRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        MailModel mailModel = MailModel.builder()
                .toName(request.to())
                .email(request.to())
                .title(request.title())
                .message(request.message())
                .senderImage(request.image())
                .sent(true)
                .build();
        return ResponseEntity.ok(mailService.create(userId, mailModel));
    }

    @Operation(summary = "Update mail", description = "Update mail flags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mail updated successfully"),
            @ApiResponse(responseCode = "404", description = "Mail not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MailModel> updateMail(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateMailRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        MailModel mailModel = MailModel.builder()
                .important(request.important())
                .starred(request.starred())
                .trash(request.trash())
                .spam(request.spam())
                .archived(request.archived())
                .build();
        return ResponseEntity.ok(mailService.update(id, userId, mailModel));
    }

    @Operation(summary = "Delete mail", description = "Delete a mail permanently")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mail deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Mail not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMail(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        mailService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete multiple mails", description = "Delete multiple mails at once")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mails deleted successfully")
    })
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteMails(
            @Valid @RequestBody BatchDeleteRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        mailService.deleteBatch(request.ids(), userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Status Toggle Endpoints ====================

    @Operation(summary = "Toggle star", description = "Toggle starred status")
    @PatchMapping("/{id}/star")
    public ResponseEntity<MailModel> toggleStar(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.toggleStar(id, userId));
    }

    @Operation(summary = "Toggle important", description = "Toggle important/bookmark status")
    @PatchMapping("/{id}/important")
    public ResponseEntity<MailModel> toggleImportant(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.toggleImportant(id, userId));
    }

    @Operation(summary = "Toggle archive", description = "Toggle archived status")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<MailModel> toggleArchive(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.toggleArchived(id, userId));
    }

    @Operation(summary = "Move to trash", description = "Move mail to trash")
    @PatchMapping("/{id}/trash")
    public ResponseEntity<MailModel> moveToTrash(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.moveToTrash(id, userId));
    }

    @Operation(summary = "Move to spam", description = "Move mail to spam")
    @PatchMapping("/{id}/spam")
    public ResponseEntity<MailModel> moveToSpam(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(mailService.moveToSpam(id, userId));
    }

    @Operation(summary = "Clear mail actions", description = "Clear all status flags from mail")
    @PatchMapping("/{id}/clear")
    public ResponseEntity<Void> clearMailActions(
            @Parameter(description = "Mail ID") @PathVariable UUID id,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        mailService.clearMailActions(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Batch Operations ====================

    @Operation(summary = "Archive multiple", description = "Archive multiple mails")
    @PostMapping("/batch/archive")
    public ResponseEntity<Void> archiveMultiple(
            @Valid @RequestBody BatchIdsRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        mailService.archiveMultiple(request.ids(), userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Spam multiple", description = "Mark multiple mails as spam")
    @PostMapping("/batch/spam")
    public ResponseEntity<Void> spamMultiple(
            @Valid @RequestBody BatchIdsRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        mailService.spamMultiple(request.ids(), userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Trash multiple", description = "Move multiple mails to trash")
    @PostMapping("/batch/trash")
    public ResponseEntity<Void> trashMultiple(
            @Valid @RequestBody BatchIdsRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        mailService.trashMultiple(request.ids(), userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Request DTOs ====================

    public record CreateMailRequest(
            @NotBlank(message = "Recipient is required")
            String to,

            @NotBlank(message = "Title is required")
            @Size(max = 500, message = "Title must be at most 500 characters")
            String title,

            @Size(max = 50000, message = "Message must be at most 50000 characters")
            String message,

            String image
    ) {}

    public record UpdateMailRequest(
            Boolean important,
            Boolean starred,
            Boolean trash,
            Boolean spam,
            Boolean archived
    ) {}

    public record BatchDeleteRequest(
            @NotNull(message = "Mail IDs are required")
            List<UUID> ids
    ) {}

    public record BatchIdsRequest(
            @NotNull(message = "Mail IDs are required")
            List<UUID> ids
    ) {}
}
