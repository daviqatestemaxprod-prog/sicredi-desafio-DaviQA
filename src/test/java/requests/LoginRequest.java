package requests;

import base.BaseRequest;
import dto.Login;
import dto.LoginResponse;
import io.restassured.response.Response;

public class LoginRequest extends BaseRequest {

    public Response realizarLoginComPayload(java.util.Map<String, String> payload) {
        return novaRequisicao().body(payload).post("/auth/login");
    }

    public Response realizarLogin(Login login) {
        return novaRequisicao()
                .body(login)
                .post("/auth/login");
    }

    /** Retorna o token puro para uso nas requisicoes autenticadas. */
    public String extrairToken(Response resposta) {
        if (resposta.statusCode() < 200 || resposta.statusCode() >= 300) {
            throw new IllegalStateException(
                    "Login sem sucesso. Status HTTP: " + resposta.statusCode());
        }

        LoginResponse login = resposta.as(LoginResponse.class);
        // Aceita token documentado e accessToken quando presente na resposta.
        String token = login.accessToken();
        if (token == null || token.isBlank()) {
            token = login.token();
        }
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Resposta de login sem token de acesso.");
        }
        return token;
    }
}
