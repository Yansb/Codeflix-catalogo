package com.yansb.catalogo.application.category.save;

import com.yansb.catalogo.application.UseCaseTest;
import com.yansb.catalogo.domain.Fixture;
import com.yansb.catalogo.domain.category.Category;
import com.yansb.catalogo.domain.category.CategoryGateway;
import com.yansb.catalogo.domain.exceptions.DomainException;
import com.yansb.catalogo.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class SaveCategoryUseCaseTest extends UseCaseTest {

    @InjectMocks
    private SaveCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Test
    public void givenValidCategory_whenCallsSave_shouldPersistIt() {
        //given
        final var aCategory = Fixture.Categories.aulas();

        when(categoryGateway.save(any()))
                .thenAnswer(returnsFirstArg());

        //when
        this.useCase.execute(aCategory);

        //then
        Mockito.verify(categoryGateway, times(1)).save(eq(aCategory));
    }

    @Test
    public void givenInvalidName_whenCallsSave_shouldPersistIt() {
        //given
        final var expectedError = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var aCategory = Category.with(
                UUID.randomUUID().toString().replace("-", ""),
                "",
                "Conteudo gravado",
                true,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        //when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        //then
        assertEquals(expectedError, actualError.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualError.getErrors().size());
        Mockito.verify(categoryGateway, times(0)).save(any());
    }

    @Test
    public void givenNullID_whenCallsSave_shouldPersistIt() {
        //given
        final var expectedError = "'id' should not be empty";
        final var expectedErrorCount = 1;

        final var aCategory = Category.with(
                null,
                "Aulas",
                "Conteudo gravado",
                true,
                InstantUtils.now(),
                InstantUtils.now(),
                null
        );

        //when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        //then
        assertEquals(expectedError, actualError.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualError.getErrors().size());
        Mockito.verify(categoryGateway, times(0)).save(any());
    }

    @Test
    public void givenNullCategory_whenCallsSave_shouldPersistIt() {
        //given
        final var expectedError = "'aCategory' cannot be null";
        final var expectedErrorCount = 1;

        final Category aCategory = null;

        //when
        final var actualError = Assertions.assertThrows(DomainException.class, () -> this.useCase.execute(aCategory));

        //then
        assertEquals(expectedError, actualError.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualError.getErrors().size());
        Mockito.verify(categoryGateway, times(0)).save(any());
    }
}
