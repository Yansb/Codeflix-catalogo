package com.yansb.catalogo.infrastructure.kafka;

import com.yansb.catalogo.AbstractEmbeddedKafka;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryListenerTest extends AbstractEmbeddedKafka {

    @Test
    public void testDummy() {
        Assertions.assertNotNull(producer());
    }

}
