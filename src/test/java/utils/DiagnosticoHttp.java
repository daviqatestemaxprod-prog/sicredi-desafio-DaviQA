package utils;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/** Registra somente metadados: nunca corpos, headers, cookies ou credenciais. */
public class DiagnosticoHttp implements Filter {
    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec, FilterContext context) {
        long inicio = System.nanoTime();
        String caminho = java.net.URI.create(request.getURI()).getPath();
        try {
            Response response = context.next(request, responseSpec);
            anexar(request.getMethod(), caminho, inicio, "HTTP " + response.statusCode());
            return response;
        } catch (RuntimeException exception) {
            anexar(request.getMethod(), caminho, inicio, exception.getClass().getSimpleName());
            throw exception;
        }
    }

    private void anexar(String metodo, String caminho, long inicio, String resultado) {
        long duracao = (System.nanoTime() - inicio) / 1_000_000;
        Allure.addAttachment("Diagnostico HTTP", "text/plain",
                metodo + " " + caminho + "\nResultado: " + resultado
                        + "\nDuracao: " + duracao + " ms", ".txt");
    }
}
