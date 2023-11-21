package com.yansb.catalogo.infrastructure.category;

import com.yansb.catalogo.domain.category.Category;

import java.util.Optional;

public interface CategoryGateway {
    Optional<Category> categoryOfId(String anId);
}
