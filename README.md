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

**Validação:** execute `mvn clean test` para obter o resultado atual. Os testes acessam a API pública e dependem da sua disponibilidade.

## 🎯 Estratégia e Plano de Testes

A estratégia foca no contrato da API, cobrindo fluxos felizes com status **200/201** e fluxos de exceção com status **400/401**. As asserções utilizam JUnit 5 e RestAssured, sem Cucumber ou Gherkin.

| Cenário | Validação implementada |
| --- | --- |
| Login válido | HTTP 200 e token de acesso preenchido |
| Login com senha inválida | HTTP 400 ou 401 e mensagem `Invalid credentials` |
| Listagem de produtos | HTTP 200 e lista não vazia |
| Cadastro de produto com token | HTTP 201, ID positivo e correspondência dos campos enviados |
| Cadastro público sem token | HTTP 201 e ID positivo |
| Listagem autenticada | HTTP 200 e lista não vazia |
| Listagem protegida sem token | HTTP 401 e mensagem `Access Token is required` |
| Listagem protegida com token inválido | HTTP 401 e mensagem `Invalid/Expired Token!` |

O Datafaker gera massa dinâmica para título, preço, descrição e quantidade. O login utiliza credenciais conhecidas para evitar depender de usuários aleatórios inexistentes. As requisições estão isoladas em `LoginRequest` e `ProdutoRequest`, que herdam a configuração comum de `BaseRequest`. `BaseTest` disponibiliza os clientes aos testes, e `ProdutoTest` obtém um token via `@BeforeEach`.

O cadastro retorna o objeto criado, sem mensagem textual de sucesso; por isso, o teste valida o ID e os dados retornados. Produtos adicionados não são persistidos pela API, portanto a suíte não depende de consultar posteriormente o produto criado.

## 🐛 Bugs Identificados

Nenhum bug funcional confirmado pelos cenários implementados. O cadastro em `/products/add` é público e simulado, conforme a documentação; retornar 201 sem token não é tratado como vulnerabilidade. As rejeições de autorização são verificadas em `/auth/products`.

A documentação do desafio apresenta exemplos de login e autenticação que podem divergir da versão atual do DummyJSON (status de login, nome do campo de token e erros de autenticação). A suíte valida login 200, aceita `token` ou `accessToken` e verifica os erros atuais. Essas diferenças devem ser tratadas como pontos de alinhamento da documentação.
## 💡 Melhorias

- Padronizar o formato e o conteúdo das mensagens de erro da API.
- Definir regras mais estritas para preço: tipo numérico, limite de casas decimais e rejeição de valores negativos ou incompatíveis.
- Alinhar com o responsável pelo requisito quais rotas devem exigir autenticação e atualizar a documentação e os testes de acordo com essa decisão.
- Ampliar a cobertura para campos obrigatórios, limites de quantidade, paginação e tokens inválidos ou expirados.
- Ampliar a cobertura para consulta de produto por ID e validação de schemas JSON.

A pipeline do GitHub gera o relatório Allure e disponibiliza o artefato `relatorios-testes`, mesmo quando os testes falham. O HTML fica em `target/site/allure-maven-plugin/index.html`.

A entrega oficial exige repositório privado no GitLab, branch `main` e convite ao avaliador como Developer. O GitHub atual serve para desenvolvimento; não substitui esses requisitos de entrega.
