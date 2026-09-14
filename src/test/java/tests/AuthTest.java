package tests;

import base.BaseTest;
import dto.Login;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import utils.DataFactory;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest extends BaseTest {

    @Test
    void naoDeveRealizarLoginComUsernameVazio() {
        Response resposta = loginRequest.realizarLogin(new Login("", DataFactory.criarLoginValido().password()));
        resposta.then().statusCode(400);
        assertEquals("Username and password required", resposta.jsonPath().getString("message"));
    }

    @Test
    void naoDeveRealizarLoginComPasswordVazio() {
        Response resposta = loginRequest.realizarLogin(new Login(DataFactory.criarLoginValido().username(), ""));
        resposta.then().statusCode(400);
        assertEquals("Username and password required", resposta.jsonPath().getString("message"));
    }

    @Test
    void naoDeveRealizarLoginSemUsername() {
        Response resposta = loginRequest.realizarLoginComPayload(java.util.Map.of(
                "password", DataFactory.criarLoginValido().password()));
        resposta.then().statusCode(400);
        assertEquals("Username and password required", resposta.jsonPath().getString("message"));
    }

    @Test
    void naoDeveRealizarLoginSemPassword() {
        Response resposta = loginRequest.realizarLoginComPayload(java.util.Map.of(
                "username", DataFactory.criarLoginValido().username()));
        resposta.then().statusCode(400);
        assertEquals("Username and password required", resposta.jsonPath().getString("message"));
    }

    @Test
    void deveRealizarLoginComSucesso() {
        Response resposta = loginRequest.realizarLogin(DataFactory.criarLoginValido());

        resposta.then().statusCode(200);
        assertFalse(loginRequest.extrairToken(resposta).isBlank(),
                "O login deve retornar um token de acesso preenchido.");
    }

    @Test
    void naoDeveRealizarLoginComSenhaInvalida() {
        Login valido = DataFactory.criarLoginValido();
        Login invalido = new Login(valido.username(), valido.password() + "-invalida");

        Response resposta = loginRequest.realizarLogin(invalido);

        assertTrue(resposta.statusCode() == 400 || resposta.statusCode() == 401,
                () -> "Esperado HTTP 400 ou 401, recebido " + resposta.statusCode());
        assertEquals("Invalid credentials", resposta.jsonPath().getString("message"));
    }
}
