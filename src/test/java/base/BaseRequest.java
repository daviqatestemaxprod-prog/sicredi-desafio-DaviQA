package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class BaseRequest {

    protected RequestSpecification novaRequisicao() {
        return RestAssured.given()
                .baseUri(System.getProperty("api.base.url", "https://dummyjson.com"))
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
}
