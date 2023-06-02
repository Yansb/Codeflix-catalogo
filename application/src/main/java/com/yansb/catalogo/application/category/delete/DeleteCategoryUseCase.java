package com.yansb.catalogo.application.category.delete;

import com.yansb.catalogo.application.UnitUseCase;
import com.yansb.catalogo.domain.category.CategoryGateway;

import java.util.Objects;

public class DeleteCategoryUseCase extends UnitUseCase<String> {

    private final CategoryGateway categoryGateway;

    public DeleteCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public void execute(String anId) {
        if (anId == null) {
            return;
        }

        this.categoryGateway.deleteById(anId);
    }
}
