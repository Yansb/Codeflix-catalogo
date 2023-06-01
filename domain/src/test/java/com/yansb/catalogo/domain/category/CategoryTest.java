package com.yansb.catalogo.domain.category;

import com.yansb.catalogo.domain.UnitTest;
import com.yansb.catalogo.domain.exceptions.DomainException;
import com.yansb.catalogo.domain.utils.InstantUtils;
import com.yansb.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class CategoryTest extends UnitTest {
    @Test
    public void givenAValidParams_whenCallWith_thenInstantiateACategory() {
        final var expectedID = UUID.randomUUID().toString();
        final var expectedName = "Movies";
        final var expectedDescription = "Most watched category";
        final var expectIsActive = true;
        final var expectedDate = InstantUtils.now();

        final var actualCategory = Category.with(expectedID, expectedName, expectedDescription, expectIsActive, expectedDate, expectedDate, null);

        Assertions.assertNotNull(actualCategory);
        Assertions.assertEquals(expectedID, actualCategory.id());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectIsActive, actualCategory.active());
        Assertions.assertEquals(expectedDate, actualCategory.createdAt());
        Assertions.assertEquals(expectedDate, actualCategory.updatedAt());
        Assertions.assertNull(actualCategory.deletedAt());
    }

    @Test
    public void givenAValidParams_whenCallWithCategory_thenInstantiateACategory() {
        final var expectedID = UUID.randomUUID().toString();
        final var expectedName = "Movies";
        final var expectedDescription = "Most watched category";
        final var expectIsActive = true;
        final var expectedDate = InstantUtils.now();

        final var aCategory = Category.with(expectedID, expectedName, expectedDescription, expectIsActive, expectedDate, expectedDate, null);

        final var actualCategory = Category.with(aCategory);
        Assertions.assertEquals(actualCategory.id(), aCategory.id());
        Assertions.assertEquals(actualCategory.name(), aCategory.name());
        Assertions.assertEquals(actualCategory.description(), aCategory.description());
    }

    @Test
    public void givenAnInvalidNullName_whenCallNewCategoryValidate_thenShouldReceiveError() {
        //given
        final String expectedName = null;
        final var expectedID = UUID.randomUUID().toString();
        final var expectedDate = InstantUtils.now();
        final var expectedDescription = "Most watched category";
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;
        final var expectIsActive = true;

        //when
        final var actualCategory = Category.with(expectedID, expectedName, expectedDescription, expectIsActive, expectedDate, expectedDate, null);

        final var actualException = Assertions.assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewCategoryValidate_thenShouldReceiveError() {
        final String expectedName = "  ";
        //given
        final var expectedID = UUID.randomUUID().toString();
        final var expectedDate = InstantUtils.now();
        final var expectedDescription = "Most watched category";
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;
        final var expectIsActive = true;

        //when
        final var actualCategory = Category.with(expectedID, expectedName, expectedDescription, expectIsActive, expectedDate, expectedDate, null);

        final var actualException = Assertions.assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    public void givenAnInvalidNullID_whenCallNewCategoryValidate_thenShouldReceiveError() {
        //given
        final var expectedName = "Aulas";
        final String expectedID = null;
        final var expectedDate = InstantUtils.now();
        final var expectedDescription = "Most watched category";
        final var expectedErrorMessage = "'id' should not be empty";
        final var expectedErrorCount = 1;
        final var expectIsActive = true;

        //when
        final var actualCategory = Category.with(expectedID, expectedName, expectedDescription, expectIsActive, expectedDate, expectedDate, null);

        final var actualException = Assertions.assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

    @Test
    public void givenAnInvalidEmptyID_whenCallNewCategoryValidate_thenShouldReceiveError() {
        //given
        final var expectedName = "Aulas";
        final var expectedID = "  ";
        final var expectedDate = InstantUtils.now();
        final var expectedDescription = "Most watched category";
        final var expectedErrorMessage = "'id' should not be empty";
        final var expectedErrorCount = 1;
        final var expectIsActive = true;

        //when
        final var actualCategory = Category.with(expectedID, expectedName, expectedDescription, expectIsActive, expectedDate, expectedDate, null);

        final var actualException = Assertions.assertThrows(DomainException.class, () -> actualCategory.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    }

}
