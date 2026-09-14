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

No GitLab, `.gitlab-ci.yml` usa Maven 3.9.9 e Java 17. Executa em pushes para `main`, merge requests destinados a `main` e execuções manuais nessa branch. O checkout é realizado pelo GitLab Runner. É necessário um runner compatível com imagens Docker, habilitado e com acesso à internet.

A pipeline tenta gerar o Allure mesmo quando os testes falham, preserva a falha do job e guarda os relatórios por 14 dias. Resultados JUnit aparecem na interface do GitLab; o HTML do Allure pode ser baixado nos artefatos do job `testes-api`. Falhas de infraestrutura ou timeout podem impedir a geração dos arquivos.

**Checklist de entrega no GitLab**

1. Criar o projeto com visibilidade **Private** e enviar o código e README para `main`.
2. Definir `main` como branch padrão.
3. Convidar `correcaoprovaqa@sicredi.com.br` (avaliador `sicredi_user`) como **Developer** e conferir o estado do convite.
4. Se a candidatura for por empresa parceira, convidar também o representante informado com papel **Developer**.
5. Conferir o job `testes-api`: resultado esperado de 12 testes aprovados e artefatos disponíveis. O sucesso local não confirma execução remota.

Referência: [instruções oficiais de entrega](https://sicredi-desafio-qe.readme.io/reference/como-entregar-o-desafio). A criação do projeto privado, os convites e a execução remota precisam ser confirmados no GitLab; a presença deste checklist não significa que foram concluídos.

**Validação:** execute `mvn clean test` para obter o resultado atual. Os testes acessam a API pública e dependem da sua disponibilidade.

## 🎯 Estratégia e Plano de Testes

A estratégia foca no contrato da API, cobrindo fluxos felizes com status **200/201** e fluxos de exceção com status **400/401**. As asserções utilizam JUnit 5 e RestAssured, sem Cucumber ou Gherkin.

| Cenário | Validação implementada |
| --- | --- |
| Login válido | HTTP 200 e token de acesso preenchido |
| Login sem username | HTTP 400 e mensagem de campos obrigatórios |
| Login sem password | HTTP 400 e mensagem de campos obrigatórios |
| Produto por ID existente | HTTP 200, ID correspondente e campos básicos válidos |
| Produto por ID inexistente | HTTP 404 e mensagem com o ID solicitado |
| Login com senha inválida | HTTP 400 ou 401 e mensagem `Invalid credentials` |
| Listagem de produtos | HTTP 200 e lista não vazia |
| Cadastro de produto com token | HTTP 201, ID positivo e correspondência dos campos enviados |
| Cadastro público sem token | HTTP 201 e ID positivo |
| Listagem autenticada | HTTP 200 e lista não vazia |
| Listagem protegida sem token | HTTP 401 e mensagem `Access Token is required` |
| Listagem protegida com token inválido | HTTP 401 e mensagem `Invalid/Expired Token!` |

O Datafaker gera massa dinâmica para título, preço, descrição e quantidade. O login utiliza credenciais conhecidas para evitar depender de usuários aleatórios inexistentes. As requisições estão isoladas em `LoginRequest` e `ProdutoRequest`, que herdam a configuração comum de `BaseRequest`. `BaseTest` disponibiliza os clientes aos testes, e `ProdutoTest` obtém um token somente nos dois cenários que o enviam. Testes públicos e de rejeição de token são independentes do login.

O cadastro retorna o objeto criado, sem mensagem textual de sucesso; por isso, o teste valida o ID e os dados retornados. Produtos adicionados não são persistidos pela API, portanto a suíte não depende de consultar posteriormente o produto criado.

## 🐛 Bugs Identificados

Nenhum bug funcional confirmado pelos cenários implementados. O cadastro em `/products/add` é público e simulado, conforme a documentação; retornar 201 sem token não é tratado como vulnerabilidade. As rejeições de autorização são verificadas em `/auth/products`.

A documentação do desafio apresenta exemplos de login e autenticação que podem divergir da versão atual do DummyJSON (status de login, nome do campo de token e erros de autenticação). A suíte valida login 200, aceita `token` ou `accessToken` e verifica os erros atuais. Essas diferenças devem ser tratadas como pontos de alinhamento da documentação.
## 💡 Melhorias

- Padronizar o formato e o conteúdo das mensagens de erro da API.
- Definir regras mais estritas para preço: tipo numérico, limite de casas decimais e rejeição de valores negativos ou incompatíveis.
- Alinhar com o responsável pelo requisito quais rotas devem exigir autenticação e atualizar a documentação e os testes de acordo com essa decisão.
- Ampliar a cobertura para campos obrigatórios, limites de quantidade, paginação e tokens inválidos ou expirados.
- Ampliar a cobertura para validação de schemas JSON.

A pipeline do GitHub gera o relatório Allure e disponibiliza o artefato `relatorios-testes`, mesmo quando os testes falham. O HTML fica em `target/site/allure-maven-plugin/index.html`.

A entrega oficial exige repositório privado no GitLab, branch `main` e convite ao avaliador como Developer. O GitHub atual serve para desenvolvimento; não substitui esses requisitos de entrega.

**Revisão técnica — 14/09/2026**

| Aspecto | Antes | Agora | Benefício |
| --- | --- | --- | --- |
| Cenários | 8 aprovados | 12 aprovados | Quatro novos cenários funcionais e de exceção |
| Login em ProdutoTest | 6 chamadas pelo BeforeEach | 2 chamadas explícitas | Falha de login não bloqueia os cenários públicos ou de rejeição de token |
| Consulta por ID | Sem cobertura | ID existente e inexistente | Verifica conteúdo básico e erro 404 |
| Login incompleto | Sem cobertura | Username e password omitidos separadamente | Confirma obrigatoriedade de cada campo; não confunde ausência com null |
| Divergências da documentação | Descrição genérica | Comparação explícita abaixo | Torna visível a decisão de contrato adotada |

Execução local em Java 17: **12 testes, 0 falhas, 0 erros, 0 ignorados**. Esse resultado comprova os cenários executados, não cobertura integral da API. O tempo depende da rede e não é usado como indicador de desempenho.

**Contrato adotado e diferenças da referência do desafio**

- [Login](https://sicredi-desafio-qe.readme.io/reference/post-auth-login): a referência apresenta 201, enquanto o login executado retorna 200. A suíte exige 200; isso é uma divergência explícita em relação ao exemplo do desafio.
- O exemplo de login contém `token`; o cliente aceita também `accessToken` para compatibilidade. Essa tolerância não equivale a validar estritamente o schema de resposta.
- [Produtos autenticados](https://sicredi-desafio-qe.readme.io/reference/get-auth-products): a referência lista 403 com `Authentication Problem` e 401 com `Invalid/Expired Token!`, sem detalhar o caso de header ausente. A execução sem token retornou 401 com `Access Token is required`, validado pela suíte. Token inválido retornou o 401 documentado.
- Login sem username ou sem password retorna 400 com `Username and password required`. A página do desafio não detalha esses erros; os testes registram o comportamento observado.
- [Produto por ID](https://sicredi-desafio-qe.readme.io/reference/get-products-id): os cenários 200 e 404 seguem os exemplos documentados. O ID 1 é a referência existente; o maior inteiro positivo é usado como inexistente na massa atual.

A referência deve ser alinhada com o responsável pelo desafio antes de tratar diferenças entre documentação e serviço como requisitos definitivos. A suíte preserva as diferenças aqui registradas, sem classificá-las automaticamente como vulnerabilidades.
