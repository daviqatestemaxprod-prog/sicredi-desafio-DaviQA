package base;

import requests.LoginRequest;
import requests.ProdutoRequest;

public abstract class BaseTest {
    protected final LoginRequest loginRequest = new LoginRequest();
    protected final ProdutoRequest produtoRequest = new ProdutoRequest();
}
