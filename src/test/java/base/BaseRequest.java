package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class BaseRequest {

    protected RequestSpecification novaRequisicao() {
        return RestAssured.given()
                .baseUri(System.getProperty("api.base.url", "https://dummyjson.com"))
                .config(io.restassured.config.RestAssuredConfig.config().httpClient(
                        io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", 10000)
                                .setParam("http.socket.timeout", 20000)))
                .filter(new utils.DiagnosticoHttp())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
}
