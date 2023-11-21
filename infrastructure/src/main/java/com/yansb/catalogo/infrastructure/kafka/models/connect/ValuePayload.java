package com.yansb.catalogo.infrastructure.kafka.models.connect;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValuePayload<T>(
        @JsonProperty("before") T before,
        @JsonProperty("after") T after,
        @JsonProperty("source") Source source,
        @JsonProperty("op") Operation op

) {
}
