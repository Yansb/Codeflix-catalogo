package com.yansb.catalogo.infrastructure.category.persistence;

import com.yansb.catalogo.domain.category.Category;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;

@Document(indexName = "categories")
public class CategoryDocument {
    @MultiField(
            mainField = @Field(type = FieldType.Text, name = "name"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private final String name;
    @Field(type = FieldType.Text, name = "description")
    private final String description;
    @Field(type = FieldType.Boolean, name = "active")
    private final Boolean active;
    @Field(type = FieldType.Date, name = "created_at")
    private final Instant createdAt;
    @Field(type = FieldType.Date, name = "updated_at")
    private final Instant updatedAt;
    @Field(type = FieldType.Date, name = "deleted_at")
    private final Instant deletedAt;
    @Id
    private String id;

    public CategoryDocument(
            final String id,
            final String name,
            final String description,
            final Boolean active,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static CategoryDocument from(final Category aCategory) {
        return new CategoryDocument(
                aCategory.id(),
                aCategory.name(),
                aCategory.description(),
                aCategory.active(),
                aCategory.createdAt(),
                aCategory.updatedAt(),
                aCategory.deletedAt()
        );
    }

    public Category toCategory() {
        return Category.with(
                id(),
                name(),
                description(),
                active(),
                createdAt(),
                updatedAt(),
                deletedAt()
        );
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Boolean active() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant deletedAt() {
        return deletedAt;
    }
}
