package com.poc.kanban.config;

import com.poc.kanban.domain.Approval;
import com.poc.kanban.domain.ApprovalRule;
import com.poc.kanban.domain.BoardTypeFeature;
import com.poc.kanban.domain.KanbanAttachment;
import com.poc.kanban.domain.KanbanBoard;
import com.poc.kanban.domain.KanbanCardHistory;
import com.poc.kanban.domain.WorkflowStep;
import com.poc.kanban.domain.WorkflowTransition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.relational.core.mapping.event.AfterConvertCallback;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class JdbcConfig {

    @Bean
    AfterConvertCallback<KanbanBoard> afterBoardConvertCallback() {
        return board -> {
            board.markNotNew();
            return board;
        };
    }

    @Bean
    AfterConvertCallback<KanbanAttachment> afterAttachmentConvertCallback() {
        return attachment -> {
            attachment.markNotNew();
            return attachment;
        };
    }

    @Bean
    AfterConvertCallback<KanbanCardHistory> afterCardHistoryConvertCallback() {
        return history -> {
            history.markNotNew();
            return history;
        };
    }

    @Bean
    AfterConvertCallback<BoardTypeFeature> afterBoardTypeFeatureConvertCallback() {
        return feature -> {
            feature.markNotNew();
            return feature;
        };
    }

    @Bean
    AfterConvertCallback<WorkflowStep> afterWorkflowStepConvertCallback() {
        return step -> {
            step.markNotNew();
            return step;
        };
    }

    @Bean
    AfterConvertCallback<WorkflowTransition> afterWorkflowTransitionConvertCallback() {
        return transition -> {
            transition.markNotNew();
            return transition;
        };
    }

    @Bean
    AfterConvertCallback<ApprovalRule> afterApprovalRuleConvertCallback() {
        return rule -> {
            rule.markNotNew();
            return rule;
        };
    }

    @Bean
    AfterConvertCallback<Approval> afterApprovalConvertCallback() {
        return approval -> {
            approval.markNotNew();
            return approval;
        };
    }

    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
            new UuidToStringConverter(),
            new StringToUuidConverter(),
            new IntegerToBooleanConverter(),
            new BooleanToIntegerConverter()
        ));
    }

    @WritingConverter
    static class UuidToStringConverter implements Converter<UUID, String> {
        @Override
        public String convert(@NonNull UUID source) {
            return source.toString();
        }
    }

    @ReadingConverter
    static class StringToUuidConverter implements Converter<String, UUID> {
        @Override
        public UUID convert(@NonNull String source) {
            return UUID.fromString(source);
        }
    }

    @ReadingConverter
    static class IntegerToBooleanConverter implements Converter<Integer, Boolean> {
        @Override
        public Boolean convert(@NonNull Integer source) {
            return source != 0;
        }
    }

    @WritingConverter
    static class BooleanToIntegerConverter implements Converter<Boolean, Integer> {
        @Override
        public Integer convert(@NonNull Boolean source) {
            return source ? 1 : 0;
        }
    }
}
