package com.yansb.catalogo.infrastructure.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yansb.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.yansb.catalogo.application.category.save.SaveCategoryUseCase;
import com.yansb.catalogo.infrastructure.category.CategoryGateway;
import com.yansb.catalogo.infrastructure.category.models.CategoryEvent;
import com.yansb.catalogo.infrastructure.configuration.json.Json;
import com.yansb.catalogo.infrastructure.kafka.models.connect.MessageValue;
import com.yansb.catalogo.infrastructure.kafka.models.connect.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CategoryListener {

    public static final TypeReference<MessageValue<CategoryEvent>> CATEGORY_MESSAGE = new TypeReference<>() {
    };
    private static final Logger LOG = LoggerFactory.getLogger(CategoryListener.class);
    private final CategoryGateway categoryGateway;
    private final SaveCategoryUseCase saveCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryListener(
            final CategoryGateway categoryGateway,
            final SaveCategoryUseCase saveCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase
    ) {
        this.saveCategoryUseCase = Objects.requireNonNull(saveCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.categories.concurrency}",
            topics = "${kafka.consumers.categories.topics}",
            groupId = "${kafka.consumers.categories.group-id}",
            id = "${kafka.consumers.categories.id}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.categories.auto-offset-reset}",
            }
    )
    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            attempts = "4",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void onMessage(@Payload final String payload, final ConsumerRecordMetadata metadata) {
        LOG.info("Message received from Kafka [topic:{}] [partition:{}][offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
        final var messagePayload = Json.readValue(payload, CATEGORY_MESSAGE).payload();
        final var op = messagePayload.op();

        if (Operation.isDelete(op)) {
            this.deleteCategoryUseCase.execute(messagePayload.before().id());
        } else {
            this.categoryGateway.categoryOfId(messagePayload.after().id())
                    .ifPresentOrElse(this.saveCategoryUseCase::execute, () -> {
                        LOG.warn("Category not found [id:{}]", messagePayload.after().id());
                    });
        }

    }

    @DltHandler
    public void onDltMessage(@Payload final String payload, final ConsumerRecordMetadata metadata) {
        LOG.warn("Message received from Kafka at DLT [topic:{}] [partition:{}][offset:{}]: {}", metadata.topic(), metadata.partition(), metadata.offset(), payload);
        final var messagePayload = Json.readValue(payload, CATEGORY_MESSAGE).payload();
        final var op = messagePayload.op();

        if (Operation.isDelete(op)) {
            this.deleteCategoryUseCase.execute(messagePayload.before().id());
        } else {
            this.categoryGateway.categoryOfId(messagePayload.after().id())
                    .ifPresentOrElse(this.saveCategoryUseCase::execute, () -> {
                        LOG.warn("Category not found [id:{}]", messagePayload.after().id());
                    });
        }
    }
}
