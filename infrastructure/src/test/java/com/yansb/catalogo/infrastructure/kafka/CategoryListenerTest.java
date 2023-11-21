package com.yansb.catalogo.infrastructure.kafka;

import com.yansb.catalogo.AbstractEmbeddedKafka;
import com.yansb.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.yansb.catalogo.application.category.save.SaveCategoryUseCase;
import com.yansb.catalogo.domain.Fixture;
import com.yansb.catalogo.infrastructure.category.CategoryGateway;
import com.yansb.catalogo.infrastructure.category.models.CategoryEvent;
import com.yansb.catalogo.infrastructure.configuration.json.Json;
import com.yansb.catalogo.infrastructure.kafka.models.connect.MessageValue;
import com.yansb.catalogo.infrastructure.kafka.models.connect.Operation;
import com.yansb.catalogo.infrastructure.kafka.models.connect.ValuePayload;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CategoryListenerTest extends AbstractEmbeddedKafka {

    @MockBean
    private SaveCategoryUseCase saveCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private CategoryListener categoryListener;


    @Value("${kafka.consumers.categories.topics}")
    private String categoryTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecordMetadata> metadata;

    @Test
    public void testCategoriesTopics() throws Exception {
        //given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.categories";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.categories-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.categories-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.categories-retry-2";
        final var expectedDltTopic = "adm_videos_mysql.adm_videos.categories-dlt";

        //when
        final var topics = admin().listTopics().listings().get().stream()
                .map(it -> it.name())
                .collect(Collectors.toSet());

        //then
        Assertions.assertTrue(topics.contains(expectedMainTopic));
        Assertions.assertTrue(topics.contains(expectedRetry0Topic));
        Assertions.assertTrue(topics.contains(expectedRetry1Topic));
        Assertions.assertTrue(topics.contains(expectedRetry2Topic));
        Assertions.assertTrue(topics.contains(expectedDltTopic));
    }

    @Test
    public void givenInvalidResponsesFromHandlerShouldRetryUntilGoesToDLT() throws Exception {
        //given
        final var expectedMaxAttempts = 4;
        final var expectedMaxDLTAttempts = 1;
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.categories";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.categories-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.categories-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.categories-retry-2";
        final var expectedDltTopic = "adm_videos_mysql.adm_videos.categories-dlt";

        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message = Json.writeValueAsString(new MessageValue<>(
                new ValuePayload<>(
                        aulasEvent,
                        aulasEvent,
                        this.aSource(),
                        Operation.DELETE
                )
        ));

        final var latch = new CountDownLatch(5);

        doAnswer(t -> {
            latch.countDown();
            if (latch.getCount() > 0) {
                throw new RuntimeException("BOOM!");
            }
            return null;
        }).when(deleteCategoryUseCase).execute(any());

        //when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));
        //then
        verify(categoryListener, times(expectedMaxAttempts)).onMessage(eq(message), metadata.capture());

        final var allMetas = metadata.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        verify(categoryListener, times(expectedMaxDLTAttempts)).onDltMessage(eq(message), metadata.capture());

        Assertions.assertEquals(expectedDltTopic, metadata.getValue().topic());

    }

    @Test
    public void givenUpdateOperationWhenProcessGoesOkShouldEndTheOperation() throws Exception {
        //given
        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message = Json.writeValueAsString(new MessageValue<>(
                new ValuePayload<>(
                        aulasEvent,
                        aulasEvent,
                        this.aSource(),
                        Operation.UPDATE
                )
        ));

        final var latch = new CountDownLatch(1);

        doAnswer(t -> {
            latch.countDown();
            return aulas;
        }).when(saveCategoryUseCase).execute(any());
        doReturn(Optional.of(aulas)).when(categoryGateway).categoryOfId(any());

        //when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));
        //then
        verify(categoryGateway, times(1)).categoryOfId(eq(aulas.id()));
        verify(saveCategoryUseCase, times(1)).execute(eq(aulas));
    }

    @Test
    public void givenCreateOperationWhenProcessGoesOkShouldEndTheOperation() throws Exception {
        //given
        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message = Json.writeValueAsString(new MessageValue<>(
                new ValuePayload<>(
                        aulasEvent,
                        aulasEvent,
                        this.aSource(),
                        Operation.CREATE
                )
        ));

        final var latch = new CountDownLatch(1);

        doAnswer(t -> {
            latch.countDown();
            return aulas;
        }).when(saveCategoryUseCase).execute(any());
        doReturn(Optional.of(aulas)).when(categoryGateway).categoryOfId(any());

        //when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));
        //then
        verify(categoryGateway, times(1)).categoryOfId(eq(aulas.id()));
        verify(saveCategoryUseCase, times(1)).execute(eq(aulas));
    }

    @Test
    public void givenDeleteOperationWhenProcessGoesOkShouldEndTheOperation() throws Exception {
        //given
        final var aulas = Fixture.Categories.aulas();
        final var aulasEvent = new CategoryEvent(aulas.id());

        final var message = Json.writeValueAsString(new MessageValue<>(
                new ValuePayload<>(
                        aulasEvent,
                        aulasEvent,
                        this.aSource(),
                        Operation.DELETE
                )
        ));

        final var latch = new CountDownLatch(1);

        doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(deleteCategoryUseCase).execute(any());
        doReturn(Optional.of(aulas)).when(categoryGateway).categoryOfId(any());

        //when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));
        //then
        verify(deleteCategoryUseCase, times(1)).execute(eq(aulas.id()));
    }
}
