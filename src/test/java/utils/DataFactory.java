package utils;

import dto.Login;
import dto.Produto;
import java.math.BigDecimal;
import net.datafaker.Faker;

public final class DataFactory {
    private static final Faker FAKER = new Faker();

    private DataFactory() {
    }

    public static Login criarLoginValido() {
        // Usuario de exemplo da documentacao atual do DummyJSON.
        return new Login(System.getProperty("api.username", "emilys"),
                System.getProperty("api.password", "emilyspass"));
    }

    public static Produto criarProdutoAleatorio() {
        return new Produto(
                FAKER.commerce().productName(),
                BigDecimal.valueOf(FAKER.number().numberBetween(100, 100000), 2),
                FAKER.lorem().sentence(),
                FAKER.number().numberBetween(1, 101));
    }
}
