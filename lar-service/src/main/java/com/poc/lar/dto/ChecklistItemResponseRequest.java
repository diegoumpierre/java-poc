package com.poc.lar.dto;

import java.util.UUID;

public record ChecklistItemResponseRequest(
    UUID itemId,
    Boolean checked,
    String photoUrl,
    String note
) {}
