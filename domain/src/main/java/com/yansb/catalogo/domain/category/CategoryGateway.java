package com.yansb.catalogo.domain.category;


import com.yansb.catalogo.domain.pagination.Pagination;

import java.util.Optional;

public interface CategoryGateway {
    Category save(Category aCategory);

    void deleteById(String anID);

    Optional<Category> findById(String anID);

    Pagination<Category> findAll(CategorySearchQuery aQuery);

}
