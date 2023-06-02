package com.yansb.catalogo.application.category.list;

import com.yansb.catalogo.application.UseCase;
import com.yansb.catalogo.domain.category.CategoryGateway;
import com.yansb.catalogo.domain.category.CategorySearchQuery;
import com.yansb.catalogo.domain.pagination.Pagination;

import java.util.Objects;

public class ListCategoryUseCase extends UseCase<CategorySearchQuery, Pagination<ListCategoryOutput>> {

    private final CategoryGateway categoryGateway;

    public ListCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Pagination<ListCategoryOutput> execute(final CategorySearchQuery aQuery) {
        return this.categoryGateway.findAll(aQuery)
                .map(ListCategoryOutput::from);
    }
}
