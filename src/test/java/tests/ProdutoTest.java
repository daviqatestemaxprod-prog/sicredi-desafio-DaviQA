package tests;

import base.BaseTest;
import dto.Produto;
import dto.ProdutoResponse;
import dto.ProdutosResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.DataFactory;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest extends BaseTest {
    private String token;

    @BeforeEach
    void autenticar() {
        Response resposta = loginRequest.realizarLogin(DataFactory.criarLoginValido());
        resposta.then().statusCode(200);
        token = loginRequest.extrairToken(resposta);
    }

    @Test
    void deveListarProdutosComSucesso() {
        Response resposta = produtoRequest.listarProdutos();

        resposta.then().statusCode(200);
        ProdutosResponse produtos = resposta.as(ProdutosResponse.class);
        assertNotNull(produtos.products());
        assertFalse(produtos.products().isEmpty());
    }

    @Test
    void deveCadastrarProdutoComSucesso() {
        Produto produto = DataFactory.criarProdutoAleatorio();

        Response resposta = produtoRequest.cadastrarProduto(produto, token);

        resposta.then().statusCode(201);
        // A API confirma a criacao com o produto e seu ID, sem campo message.
        ProdutoResponse criado = resposta.as(ProdutoResponse.class);
        assertNotNull(criado.id());
        assertTrue(criado.id() > 0);
        assertEquals(produto.nome(), criado.nome());
        assertNotNull(criado.preco());
        assertEquals(0, produto.preco().compareTo(criado.preco()));
        assertEquals(produto.descricao(), criado.descricao());
        assertEquals(produto.quantidade(), criado.quantidade());
    }

    @Test
    void naoDeveCadastrarProdutoSemToken() {
        // Requisito solicitado: 401. A rota publica /products/add pode retornar 201.
        // Manter ativo para evidenciar a divergencia, sem simular uma rejeicao.
        Response resposta = produtoRequest.cadastrarProdutoSemToken(
                DataFactory.criarProdutoAleatorio());

        resposta.then().statusCode(401);
        // Nao existe mensagem de 401 documentada para esta rota publica.
        String mensagem = resposta.jsonPath().getString("message");
        assertNotNull(mensagem, "A rejeicao deve informar uma mensagem de erro.");
        assertFalse(mensagem.isBlank(), "A mensagem de erro deve estar preenchida.");
    }
}
