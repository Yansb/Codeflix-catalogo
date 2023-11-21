package com.yansb.catalogo.infrastructure.kafka.models.connect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Operation {
    CREATE("c"),
    UPDATE("u"),
    DELETE("d");

    private final String op;

    Operation(String op) {
        this.op = op;
    }

    @JsonCreator
    public static Operation of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.op.equals(value))
                .findFirst()
                .orElse(null);
    }

    public static boolean isDelete(final Operation op) {
        return DELETE.equals(op);
    }

    @JsonValue
    public String op() {
        return op;
    }
}
