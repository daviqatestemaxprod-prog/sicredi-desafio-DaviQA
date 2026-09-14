package requests;

import base.BaseRequest;
import dto.Produto;
import io.restassured.response.Response;

public class ProdutoRequest extends BaseRequest {

    public Response listarProdutosProtegidosSemToken() {
        return novaRequisicao().get("/auth/products");
    }

    public Response cadastrarProdutoSemToken(Produto produto) {
        return novaRequisicao()
                .body(produto)
                .post("/products/add");
    }

    public Response listarProdutos() {
        return novaRequisicao().get("/products");
    }

    public Response listarProdutosAutenticados(String token) {
        return novaRequisicao()
                .header("Authorization", "Bearer " + token)
                .get("/auth/products");
    }

    /** Envia o token puro, sem o prefixo Bearer. O cadastro nao persiste na API. */
    public Response cadastrarProduto(Produto produto, String token) {
        return novaRequisicao()
                .header("Authorization", "Bearer " + token)
                .body(produto)
                .post("/products/add");
    }
}
