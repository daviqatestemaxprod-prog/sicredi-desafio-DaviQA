# 🧪 Desafio Técnico QA Engineer — Sicredi

## 📌 Sobre o Projeto

Projeto do desafio técnico para QA Engineer do Sicredi, desenvolvido em **Java 17**, com **RestAssured** e **JUnit 5**, para automação de testes das rotas de autenticação e produtos da API DummyJSON. Utiliza Java Records e Jackson para os payloads, Datafaker para geração de dados e Allure Reports para relatórios de execução.

Referência: [documentação do desafio](https://sicredi-desafio-qe.readme.io/llms.txt).

## ▶️ Como Executar

Pré-requisitos: JDK 17 e Maven instalados, `JAVA_HOME` configurado, comandos `java` e `mvn` disponíveis no PATH e acesso à internet para baixar dependências e acessar a API.

Na raiz do projeto, execute:

```bash
mvn clean test
```

Por padrão, a URL é `https://dummyjson.com` e o login utiliza o usuário público de exemplo `emilys`, com senha `emilyspass`. A URL pode ser alterada pela propriedade `api.base.url`; as credenciais, por `api.username` e `api.password`.

Após a execução, os resultados do Allure ficam em `target/allure-results`. Para gerar o relatório HTML:

```bash
mvn allure:report
```

Para gerar e abrir o relatório em um servidor local:

```bash
mvn allure:serve
```

Execute o comando do Allure separadamente mesmo se houver falhas nos testes. Não execute `clean` entre os testes e a geração do relatório, pois ele apaga os resultados.

A integração contínua em `.github/workflows/ci.yml` executa `mvn clean test` com Java 17 em pushes para `main` e em pull requests destinados a `main`. Qualquer falha de teste reprova o job.

**Estado da validação:** os testes ainda não foram executados neste ambiente, pois Maven não estava disponível no PATH. O cenário de cadastro sem token exige 401 e pode falhar contra a rota pública, conforme a ressalva abaixo.

## 🎯 Estratégia e Plano de Testes

A estratégia foca no contrato da API, cobrindo fluxos felizes com status **200/201** e fluxos de exceção com status **400/401**. As asserções utilizam JUnit 5 e RestAssured, sem Cucumber ou Gherkin.

| Cenário | Validação implementada |
| --- | --- |
| Login válido | HTTP 200 e token de acesso preenchido |
| Login com senha inválida | HTTP 400 ou 401 e mensagem `Invalid credentials` |
| Listagem de produtos | HTTP 200 e lista não vazia |
| Cadastro de produto com token | HTTP 201, ID positivo e correspondência dos campos enviados |
| Cadastro de produto sem token | HTTP 401 e mensagem de erro preenchida; requisito adicional em divergência com a documentação |

O Datafaker gera massa dinâmica para título, preço, descrição e quantidade. O login utiliza credenciais conhecidas para evitar depender de usuários aleatórios inexistentes. As requisições estão isoladas em `LoginRequest` e `ProdutoRequest`, que herdam a configuração comum de `BaseRequest`. `BaseTest` disponibiliza os clientes aos testes, e `ProdutoTest` obtém um token via `@BeforeEach`.

O cadastro retorna o objeto criado, sem mensagem textual de sucesso; por isso, o teste valida o ID e os dados retornados. Produtos adicionados não são persistidos pela API, portanto a suíte não depende de consultar posteriormente o produto criado.

## 🐛 Bugs Identificados

**Cadastro sem autenticação — divergência do requisito de segurança solicitado**

> O endpoint POST `/products/add` está público. É possível cadastrar produtos sem o envio de um token de autorização, o que fere a segurança da aplicação.

**Ressalva:** essa descrição representa o requisito de segurança proposto para a suíte. A [documentação do desafio](https://sicredi-desafio-qe.readme.io/reference/post-products.md) apresenta o cadastro sem autenticação obrigatória. Portanto, o comportamento não constitui, por si só, um bug confirmado contra o contrato documentado; a exigência de autenticação deve ser validada com o responsável pelo requisito. Não houve confirmação por execução neste ambiente.

- **Passos para verificar:** enviar um produto válido para `POST /products/add`, sem o header `Authorization`.
- **Esperado pelo requisito solicitado:** HTTP 401 e mensagem de erro.
- **Comportamento descrito pela documentação:** criação simulada com retorno do produto e HTTP 201, sem persistência.
- **Cobertura:** `naoDeveCadastrarProdutoSemToken` permanece ativo e exige 401, para evidenciar a divergência quando a suíte for executada. Não há mensagem específica de 401 documentada para essa rota; o teste verifica que a mensagem está preenchida.

## 💡 Melhorias

- Padronizar o formato e o conteúdo das mensagens de erro da API.
- Definir regras mais estritas para preço: tipo numérico, limite de casas decimais e rejeição de valores negativos ou incompatíveis.
- Alinhar com o responsável pelo requisito quais rotas devem exigir autenticação e atualizar a documentação e os testes de acordo com essa decisão.
- Ampliar a cobertura para campos obrigatórios, limites de quantidade, paginação e tokens inválidos ou expirados.
- Publicar os resultados do Allure como artefatos da pipeline, inclusive quando houver falhas nos testes.
