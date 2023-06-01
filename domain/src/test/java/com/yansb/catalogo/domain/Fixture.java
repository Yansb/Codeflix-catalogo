package com.yansb.catalogo.domain;

import com.yansb.catalogo.domain.category.Category;
import com.yansb.catalogo.domain.utils.InstantUtils;
import net.datafaker.Faker;

import java.util.UUID;

public final class Fixture {
    public static final Faker FAKER = new Faker();

    public static String name() {
        return FAKER.name().fullName();
    }

    public static String id() {
        return FAKER.idNumber().valid();
    }

    public static String title() {
        return FAKER.book().title();
    }

    public static Integer year() {
        return FAKER.number().numberBetween(1900, 2050);
    }

    public static Double duration() {
        return FAKER.options().option(120.0, 15.5, 35.5, 10.0, 2.0);
    }

    public static boolean bool() {
        return FAKER.bool().bool();
    }

    public static final class Categories {
        public static Category aulas() {
            return Category.with(
                    UUID.randomUUID().toString().replace("-", ""),
                    "Aulas",
                    "Conteudo gravado",
                    true,
                    InstantUtils.now(),
                    InstantUtils.now(),
                    null
            );
        }

        public static Category lives() {
            return Category.with(
                    UUID.randomUUID().toString().replace("-", ""),
                    "Lives",
                    "Conteudo ao vivo",
                    true,
                    InstantUtils.now(),
                    InstantUtils.now(),
                    null
            );
        }
    }

}
