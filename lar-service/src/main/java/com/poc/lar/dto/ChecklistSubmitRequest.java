package com.poc.lar.dto;

import java.util.List;
import java.util.UUID;

public record ChecklistSubmitRequest(
    UUID templateId,
    List<ChecklistItemResponseRequest> items
) {}
