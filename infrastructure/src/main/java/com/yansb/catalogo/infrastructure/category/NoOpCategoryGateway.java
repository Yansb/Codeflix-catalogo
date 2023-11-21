package com.yansb.catalogo.infrastructure.category;

import com.yansb.catalogo.domain.category.Category;
import com.yansb.catalogo.domain.utils.InstantUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NoOpCategoryGateway implements CategoryGateway {
    @Override
    public Optional<Category> categoryOfId(String anId) {

        return Optional.of(
                Category.with(anId, "Lives", null, true, InstantUtils.now(), InstantUtils.now(), null)
        );
    }
}
