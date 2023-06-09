package com.yansb.catalogo.application.category.list;

import com.yansb.catalogo.domain.category.Category;

public record ListCategoryOutput(
        String id,
        String name,
        String description
) {
    public static ListCategoryOutput from(final Category aCategory) {
        return new ListCategoryOutput(aCategory.id(), aCategory.name(), aCategory.description());
    }
}
