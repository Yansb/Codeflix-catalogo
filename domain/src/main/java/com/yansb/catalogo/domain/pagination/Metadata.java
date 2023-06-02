package com.yansb.catalogo.domain.pagination;

public record Metadata(
        int currentPage,
        int perPage,
        long total
) {
}
