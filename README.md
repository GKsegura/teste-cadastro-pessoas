# Teste Técnico - Cadastro de Pessoas

Aplicação full-stack para cadastro, listagem e exclusão de pessoas, desenvolvida
como teste técnico para a vaga de Desenvolvedor Júnior (Java + Vue.js).

![Tela da aplicação](docs/tela.png)

## Stack

**Backend**

- Java 25 (Temurin) · Spring Boot 4.1.0 · Maven
- Spring Web · Spring Data JPA · Bean Validation
- MySQL · Swagger (springdoc-openapi)

**Frontend**

- Vue 3 (Composition API) · Vite · Vue Router
- Bootstrap 5
- Axios · vue-toastification · maska (máscara de CPF/CNPJ e telefone)

## Pré-requisitos

- Java 25 (Temurin) — versão usada no desenvolvimento e configurada no projeto
- Node.js 18+
- MySQL rodando em `localhost:3306`

## Como executar

### 1. Backend

O `application.properties` já vem configurado com as credenciais padrão de
desenvolvimento (`root`/`root`) - ajuste em
`src/main/resources/application.properties` apenas se as suas credenciais
locais do MySQL forem diferentes. O database `cadastro_pessoas` é criado
automaticamente na primeira execução.

Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8081`.
Documentação interativa (Swagger): **http://localhost:8081/swagger-ui.html**

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

A aplicação abre em `http://localhost:5174`.

## Endpoints

| Método | Rota            | Descrição            | Sucesso | Erros                                                    |
| ------ | --------------- | --------------------- | ------- | --------------------------------------------------------- |
| POST   | `/pessoas`      | Cadastra uma pessoa   | 201     | 400 (validação), 409 (CPF/CNPJ já cadastrado)            |
| GET    | `/pessoas`      | Lista as pessoas, paginada, com busca opcional por nome/CPF (params `busca`, `page`, `size`) | 200 | - |
| GET    | `/pessoas/{id}` | Busca uma pessoa      | 200     | 404 (id inexistente)                                       |
| PUT    | `/pessoas/{id}` | Atualiza uma pessoa   | 200     | 400 (validação), 404 (id inexistente), 409 (CPF/CNPJ já cadastrado) |
| DELETE | `/pessoas/{id}` | Exclui uma pessoa     | 204     | 404 (id inexistente)                                       |
| GET    | `/pessoas/existe-cpf-cnpj` | Verifica se um CPF/CNPJ já está cadastrado (params `cpfCnpj`, `idIgnorar` opcional) | 200 (`true`/`false`) | -   |

![Documentação Swagger](docs/swagger.png)

Campos da pessoa: **Nome Completo**, **CPF/CNPJ**, **Telefone**, **E-mail**.

Validações (Bean Validation no DTO): todos os campos são obrigatórios; e-mail
precisa ser válido; telefone precisa seguir o formato `(00) 00000-0000` ou
`(00) 0000-0000`; CPF/CNPJ é validado por um `ConstraintValidator` customizado
que confere o dígito verificador (mesmo algoritmo usado pela Receita Federal)
e não aceita duplicidade. Erros de validação retornam `400` com um mapa
`campo → mensagem`; id inexistente retorna `404`; CPF/CNPJ duplicado retorna
`409` - todos tratados por um handler global (`@RestControllerAdvice`).

![Validação de ponta a ponta](docs/validacao.png)

## Funcionalidades

- ✅ Cadastrar pessoa (com validação em duas camadas: frontend e backend)
- ✅ Listar pessoas cadastradas
- ✅ Editar pessoa cadastrada
- ✅ Excluir pessoa com confirmação prévia
- ✅ Mensagem de sucesso no cadastro (toast)
- ✅ Feedback de erro com as mensagens reais da API (toast)

## Decisões técnicas

- **Arquitetura em camadas** (`entities`, `repositories`, `dtos`, `services`,
  `controllers`, `exceptions`, `config`): responsabilidade única por camada,
  código testável e fácil de evoluir.
- **DTOs (records) em vez de expor entities**: controlam o contrato da API,
  desacoplam REST do modelo do banco e concentram as validações de entrada.
- **Handler global de exceções**: respostas de erro consistentes (400 com mapa
  de mensagens, 404 com descrição) sem try-catch espalhado pelos controllers.
- **Banco como única fonte de verdade no frontend**: após cadastrar/excluir,
  a lista é recarregada do servidor em vez de atualizada localmente -
  simplicidade e consistência para o escopo.
- **Chamadas HTTP centralizadas** em um service (`pessoaService.js`) com
  `baseURL` única, fora dos componentes.
- **vue-toastification para os toasts**: avaliei implementar manualmente
  (ref + v-if + setTimeout) e optei pela biblioteca por maturidade
  (empilhamento, acessibilidade, pausa no hover).
- **Componentização do frontend** (`PessoaForm` / `PessoaTable` comunicando
  por props/emits): inicialmente era um componente único, dado o escopo de
  uma tela só. Alterado para componentização após feedback do tech lead do
  time de produtos.
- **Identidade visual**: paleta baseada nas cores da Nexum
  (teal `#00C6B9` e grafite `#424141`), aplicada sobre os componentes padrão
  do Bootstrap.
- **Bootstrap em vez de uma lib de componentes (PrimeVue)**: o enunciado
  completo do desafio marca Bootstrap como estilização obrigatória; a versão
  inicial do projeto tinha sido feita a partir de um resumo do desafio que
  não trazia esse requisito. O formulário, a tabela e o modal de confirmação
  foram reescritos com markup e classes nativas do Bootstrap 5
  (`bootstrap.Modal` controlado via `<script setup>` para o modal de
  exclusão).
- **Validador customizado de CPF/CNPJ** (`@CpfOuCnpj`, em
  `validations/`): Bean Validation não tem suporte nativo a documentos
  brasileiros. O validador identifica CPF (11 dígitos) ou CNPJ (14 dígitos)
  pelo tamanho e confere o dígito verificador de cada um. Duplicidade é
  checada à parte no service (`existsByCpfCnpj`), retornando `409` em vez de
  deixar a constraint `unique` do banco estourar um erro `500`.
- **Rotas dedicadas para listagem/cadastro/edição** (Vue Router: `/`,
  `/pessoas/novo`, `/pessoas/:id/editar`): o desafio pede redirecionamento
  para a listagem após salvar - modelar isso como navegação de URL (em vez de
  alternar componentes na mesma tela) é mais fiel ao requisito e permite
  acessar a edição de uma pessoa diretamente pela URL (usa
  `GET /pessoas/{id}` para carregar os dados nesse caso).
- **Verificação de CPF/CNPJ duplicado em tempo real** (`GET
  /pessoas/existe-cpf-cnpj`): o frontend chama esse endpoint com debounce
  assim que o campo CPF/CNPJ é preenchido por completo (11 ou 14 dígitos),
  avisando o usuário antes do envio. No modo de edição, o parâmetro opcional
  `idIgnorar` exclui a própria pessoa da checagem (reaproveita
  `existsByCpfCnpjAndIdNot`, já usado em `atualizar`). É só uma otimização de
  UX - o backend continua validando a duplicidade no `POST`/`PUT` e retornando
  `409`, então a checagem prévia falhar silenciosamente (rede indisponível,
  etc.) não compromete a integridade dos dados.
- **Validação de nome × CPF/CNPJ avaliada e descartada**: não existe base
  gratuita ou pública para confirmar que um nome corresponde a um CPF. A
  Receita Federal não expõe API para isso (o serviço público exige CPF + data
  de nascimento, tem captcha e devolve o nome mascarado, sem aceitar um nome
  como entrada para conferência); provedores como Serpro (Datavalid),
  Assertiva ou BigDataCorp oferecem esse tipo de consulta, mas são pagos por
  requisição, exigem contrato/CNPJ e são desproporcionais ao escopo deste
  teste. Cruzar nome e CPF de terceiros sem base legal também esbarra na
  LGPD. A validação de CPF/CNPJ ficou restrita ao dígito verificador
  (`@CpfOuCnpj`).
- **Busca e paginação no backend, não no frontend**: `GET /pessoas` aceita
  `busca` (casa com nome ou CPF/CNPJ, via
  `findByNomeContainingIgnoreCaseOrCpfCnpjContainingIgnoreCase`), `page` e
  `size`, devolvendo `Page` do Spring Data serializado como `PagedModel`
  (`{ content, page: { size, number, totalElements, totalPages } }`). Filtrar
  e paginar no banco evita trafegar e reprocessar a lista inteira a cada
  busca - decisão que só importa em escala, mas é o padrão correto do Spring
  Data e não tem custo extra de implementação.
- **maska para as máscaras de CPF/CNPJ e telefone** (`v-maska`, em
  `PessoaForm.vue`): substituiu a formatação manual em regex de
  `utils/mascaras.js`. Suporta máscara dinâmica (array de padrões) e escolhe
  automaticamente o padrão que cabe no que foi digitado - por isso o mesmo
  campo alterna corretamente entre CPF (11 dígitos) e CNPJ (14 dígitos), e
  entre telefone fixo (10 dígitos) e celular (11 dígitos), sem lógica
  condicional própria. Biblioteca pequena (~3kb), sem dependências e com
  integração nativa a `v-model`.
- **Testes unitários do `PessoaService`** (JUnit 5 + Mockito, em
  `PessoaServiceTest`): repositório mockado para cobrir as regras de negócio
  isoladas de banco - cadastro/atualização bloqueados por CPF/CNPJ duplicado,
  busca por id inexistente, e os dois caminhos de `existeCpfCnpj` (com e sem
  `idIgnorar`). Rodar com `./mvnw test` exigiu registrar o Mockito como
  `-javaagent` explícito no `maven-surefire-plugin` (via
  `maven-dependency-plugin:properties`, que expõe o caminho do jar do
  Mockito como propriedade Maven): no Java 25 a JVM não permite mais que o
  Mockito se auto-anexe como agente em tempo de execução, então o mock maker
  inline falha ao inicializar sem esse `-javaagent` no `argLine`.

---

Desenvolvido por **[José Segura](https://gksegura.netlify.app)** ·
[GitHub](https://github.com/GKsegura)
