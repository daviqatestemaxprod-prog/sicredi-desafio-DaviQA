package tests;

import base.BaseTest;
import dto.Produto;
import dto.ProdutoResponse;
import dto.ProdutosResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import utils.DataFactory;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest extends BaseTest {
    private String autenticar() {
        Response resposta = loginRequest.realizarLogin(DataFactory.criarLoginValido());
        resposta.then().statusCode(200);
        return loginRequest.extrairToken(resposta);
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

        Response resposta = produtoRequest.cadastrarProduto(produto, autenticar());

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
    void deveCadastrarProdutoSemTokenEmRotaPublica() {
        Response resposta = produtoRequest.cadastrarProdutoSemToken(
                DataFactory.criarProdutoAleatorio());

        resposta.then().statusCode(201);
        ProdutoResponse criado = resposta.as(ProdutoResponse.class);
        assertNotNull(criado.id());
        assertTrue(criado.id() > 0);
    }

    @Test
    void deveListarProdutosComTokenValido() {
        Response resposta = produtoRequest.listarProdutosAutenticados(autenticar());
        resposta.then().statusCode(200);
        ProdutosResponse produtos = resposta.as(ProdutosResponse.class);
        assertNotNull(produtos.products());
        assertFalse(produtos.products().isEmpty());
    }

    @Test
    void naoDeveListarProdutosProtegidosSemToken() {
        Response resposta = produtoRequest.listarProdutosProtegidosSemToken();
        resposta.then().statusCode(401);
        assertEquals("Access Token is required", resposta.jsonPath().getString("message"));
    }

    @Test
    void naoDeveListarProdutosProtegidosComTokenInvalido() {
        Response resposta = produtoRequest.listarProdutosAutenticados("token-invalido");
        resposta.then().statusCode(401);
        assertEquals("Invalid/Expired Token!", resposta.jsonPath().getString("message"));
    }

    @Test
    void deveBuscarProdutoPorIdExistente() {
        Response resposta = produtoRequest.buscarProdutoPorId(1);
        resposta.then().statusCode(200);
        ProdutoResponse produto = resposta.as(ProdutoResponse.class);
        assertEquals(1, produto.id());
        assertNotNull(produto.nome());
        assertFalse(produto.nome().isBlank());
        assertNotNull(produto.preco());
        assertTrue(produto.preco().signum() >= 0);
        assertNotNull(produto.quantidade());
        assertTrue(produto.quantidade() >= 0);
    }

    @Test
    void naoDeveBuscarProdutoPorIdInexistente() {
        int id = Integer.MAX_VALUE;
        Response resposta = produtoRequest.buscarProdutoPorId(id);
        resposta.then().statusCode(404);
        assertEquals("Product with id '" + id + "' not found", resposta.jsonPath().getString("message"));
    }
}
