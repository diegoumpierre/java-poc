package com.poc.kanban.model;

import java.util.List;

public record BoardTypeModel(
        String code,
        String name,
        String productCode,
        Boolean visibleInKanban,
        Boolean singleton,
        String scope,
        Boolean autoCreate,
        String numberPrefix,
        Boolean allowCustomLists,
        List<String> enabledFeatures
) {}
