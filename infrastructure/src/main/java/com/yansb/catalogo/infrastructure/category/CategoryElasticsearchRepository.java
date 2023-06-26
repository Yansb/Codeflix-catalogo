package com.yansb.catalogo.infrastructure.category;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryElasticsearchRepository extends ElasticsearchRepository<CategoryDocument, String> {
}
