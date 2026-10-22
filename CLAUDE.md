# CLAUDE.md

Este arquivo orienta o Claude Code (claude.ai/code) ao trabalhar com o código deste repositório.

## Idioma

- Converse com o usuário **sempre em português**.
- Toda documentação (README, CLAUDE.md, comentários, mensagens da API, mensagens de commit, textos do TCC) deve ser escrita **em português**.
- Identificadores no código também seguem o português (`Produto`, `Remedio`, `listar`...). Mantenha essa convenção.

## Branch de trabalho

O desenvolvimento acontece na branch **`tcc2/sprint1-programacao`**. A `main` está desatualizada em relação a ela e **não deve ser alterada**: nada de commits, merges ou push na `main`, nem criar trabalho novo a partir dela. A branch padrão no GitHub continua sendo a `main`, por decisão do autor; não a troque sem que ele peça.

## Projeto

**Dr.StoreSys** é um sistema de gestão para drogarias desenvolvido como TCC do curso de Análise e Desenvolvimento de Sistemas do IFAM.

### Documentos em `docs/`
- `TCC-II_MONOGRAFIA_desenvolvimento.pdf`: **a nossa monografia**, em escrita. Ela define o escopo do sistema.
- `ANTEPROJETO_TCC1_...pdf`: o nosso anteprojeto (TCC I), a proposta original do sistema de gestão de vendas, estoque e preços. Quando divergir da monografia, vale a monografia.
- `TCC2_Harley_ Final_v2.pdf`: monografia de **outro autor**. Serve apenas de **exemplo de estrutura e formatação** para escrevermos a nossa. **Não** a use como referência sobre o nosso sistema (requisitos, modelo de dados, regras de negócio).

### Escopo definido na monografia
- **Público-alvo**: drogarias de **pequeno porte**, geralmente em bairros residenciais. Ficam de fora as grandes redes e as soluções apenas contábeis ou financeiras. O foco é a operação diária do **ponto de venda (PDV)**.
- **Problema**: esses estabelecimentos têm estoques pequenos, mas complexos e regulados pela Anvisa, e costumam operar sem Procedimentos Operacionais Padrão (POPs). Também dependem de métodos manuais, planilhas ou PDVs genéricos, o que gera falhas no registro de vendas, no controle de estoque e nos preços.
- **Objetivo geral**: analisar e desenvolver um protótipo de sistema integrado de gestão para drogarias de pequeno porte.
- **Objetivos específicos**:
  - levantar requisitos funcionais e não funcionais para automatizar o **registro de vendas**, o **controle de estoque** e a **precificação**;
  - projetar a arquitetura e os modelos do sistema: diagramas de caso de uso, de classes e entidade-relacionamento;
  - implementar um **MVP** que integre vendas, estoque e preços.
- **Situação da monografia**: introdução, problematização, justificativa e objetivos estão escritos. Os trechos em vermelho e laranja dos objetivos parecem ainda estar em revisão. Metodologia (1.4), organização do documento (1.5), fundamentação teórica (2), implementação da solução (3), considerações finais (4) e referências ainda estão vazios.

Para requisitos, modelo de dados e regras de negócio já implementados, a fonte da verdade é o código deste repositório. Para o que o sistema deve cobrir, a referência é a nossa monografia.

São duas aplicações independentes, sem build na raiz:
- `backend/`: API REST em Spring Boot 3.4, Java 21, Maven, MySQL e springdoc/Swagger.
- `frontend/`: React 19 + Vite em JavaScript puro (JSX), com axios, react-router-dom, react-select e react-toastify.
- `docs/`: anteprojeto e monografias (veja acima).
- `dbconfig/testes.sql`: SELECTs avulsos para inspecionar tabelas. Não é schema nem migration.

## Comandos

Backend (executar em `backend/`; no Windows use `mvnw.cmd`):
```
./mvnw spring-boot:run                 # sobe a API na porta 8080
./mvnw test                            # roda os testes
./mvnw test -Dtest=TccApplicationTests # uma classe de teste (acrescente #metodo para um único método)
./mvnw package                         # gera o jar
```
Swagger UI: http://localhost:8080/swagger-ui.html

Frontend (executar em `frontend/`):
```
npm install
npm run dev      # servidor Vite na porta 5173, aberto na rede (host: true)
npm run lint     # ESLint
npm run build
```

## Ambiente e armadilhas

- **Banco de dados**: MySQL em `localhost`, schema `drStoreSys` (criado automaticamente). As credenciais ficam em `backend/src/main/resources/application.properties`. Com `ddl-auto=update` não há migrations: o schema é gerado a partir das entidades JPA. O H2 está no classpath de teste, mas não existe perfil de teste, então o único teste (`contextLoads`) precisa do MySQL rodando.
- **`ddl-auto=update` não remove colunas**: ao renomear ou apagar um campo de uma entidade, a coluna antiga continua no banco. Se ela for `NOT NULL`, os inserts passam a falhar com "Field '...' doesn't have a default value" (aconteceu com `preco`, que tinha `data_vigencia`, `preco_custo` e `is_ativo` da versão antiga). Nesse caso, apague a coluna ou a tabela à mão.
- **Dados iniciais**: `config/DataLoader.java` (um `CommandLineRunner`) popula as tabelas de apoio (controle de receita, medidas básicas/farmacêuticas/padrão, apresentações) e cria um exemplo de princípio ativo, composição, fórmula e laboratório. Cada tabela só é populada se estiver vazia. Novos valores de apoio devem ser adicionados ali.
- **MySQL 8.4+ / Docker**: a URL do banco já inclui `allowPublicKeyRetrieval=true`, necessário para a autenticação `caching_sha2_password` com `useSSL=false`. Sem MySQL instalado, dá para subir um container: `docker run -d --name drstoresys-mysql -e MYSQL_ROOT_PASSWORD=<senha do application.properties> -p 3306:3306 mysql:8.4`.
- **URL do backend no frontend**: `frontend/src/services/api.js` lê `VITE_API_URL` e, se ela não existir, usa `http://localhost:8080`. Para apontar para outra máquina (ex.: um IP da rede local), crie `frontend/.env.local` (ignorado pelo git) com `VITE_API_URL=http://<ip>:8080`. O CORS do backend (`config/CorsConfig.java`) libera qualquer origem (`*`).
- **Segurança** (`config/SecurityConfig.java`): CSRF desativado e todas as requisições liberadas (`permitAll`). Ainda não há autenticação.
- **Validação**: os DTOs usam anotações `jakarta.validation` e os controllers usam `@Valid`, mas o `spring-boot-starter-validation` não está no `pom.xml`. Provavelmente essas restrições não são aplicadas em tempo de execução.
- **Testes**: só existe o teste de carregamento do contexto Spring. O frontend não tem testes.

## Arquitetura do backend

Pacote raiz: `com.josepacheco.tcc`. As camadas são `controller → service → repository → model`, com classes `dto` entre controller e service. Todas as camadas repetem os mesmos subpacotes de domínio: `produto`, `produto/remedio/atributos`, `produto/remedio/formulacao` e `preco`.

Os módulos de produto e de preço estão implementados. `model/estoque` e `model/venda` (e seus repositories) existem, mas ainda não têm services nem controllers. No frontend, os menus correspondentes mostram o aviso "em construção".

### Módulo de preço
- `Preco` guarda um **histórico**: cada alteração cria um registro novo (`produto`, `valorVenda` em `BigDecimal`, `dataInicioVigencia`), e nada é sobrescrito. O preço atual é o registro mais recente do produto (`findTopByProdutoEanOrderByDataInicioVigenciaDesc`).
- Rotas em `/api/preco`, identificando o produto pelo EAN:
  - `POST /api/preco`, com corpo `{ ean, valorVenda }`; a vigência começa no momento do cadastro;
  - `GET /api/preco/{ean}/atual`;
  - `GET /api/preco/{ean}/historico`.
- O `PrecoController` não segue o modelo dos controllers de produto (sem try/catch nem anotações Swagger): os erros saem do service como `ResponseStatusException` (404 para produto ou preço inexistente, 400 para valor ≤ 0).
- Os campos `precoCusto` e `isAtivo` estão comentados na entidade.

### Modelo de domínio (módulo de produto)
- `Produto` usa **herança em tabela única** com a coluna discriminadora `tipo_produto`: `Produto` (`"geral"`) ← `Remedio` (`"remedio"`) ← `Generico` (`"generico"`). Externamente os produtos são identificados pelo **EAN** (único), não pelo id. O `ProdutoController` atualiza via `PUT /api/produto/{ean}`.
- `Remedio` referencia as entidades de apoio `Formula`, `Laboratorio`, `MedidaFarmaceutica` (unidade da dose), `MedidaBasica` (unidades de conteúdo e peso), `Apresentacao` e `ControleReceita`. Todo produto também tem uma `MedidaPadrao` (unidade de venda, como CX ou UN).
- Cadeia de formulação: `PrincipioAtivo` → `Composicao` (princípio + quantidade + `MedidaBasica`) → `Formula` (lista muitos-para-muitos de `Composicao`, tabela de junção `formula_composicao`).
- `Composicao.toString()` monta o rótulo legível (ex.: "dipirona monoidratada 1g") que o `FormulaOutputDTO` expõe. O construtor completo de `Generico` tenta usar `Formula.toString()` como nome, mas `Formula` não sobrescreve esse método. Esse construtor também não é usado pelo fluxo da API (`RemedioInputDTO.build` usa `new Generico()`), então o genérico fica com o `nomeComercial` enviado pelo cliente.

### Convenções dos DTOs (importante ao criar endpoints)
- Entidades simples usam uma única classe DTO. Entidades com relacionamentos são divididas em `XxxInputDTO` e `XxxOutputDTO`.
- Os DTOs de entrada constroem a entidade num método `build(...)`. Os relacionamentos chegam na requisição como chaves legíveis (siglas, nomes, ids), e os repositories são passados como argumentos para o `build` resolvê-los. Exemplo: `remedioInputDTO.build(formulaRepository, laboratorioRepository, ...)`. Os DTOs de saída são construídos a partir da entidade (`new XxxDTO(entidade)`).
- O `ProdutoDTO` é **polimórfico via Jackson**: `@JsonTypeInfo` na propriedade `tipoProduto`, com os subtipos `geral`, `remedio` e `generico` mapeados para `ProdutoDTO`, `RemedioInputDTO` e `GenericoInputDTO`. `ProdutoService.create/update` decide o caminho com `instanceof`, e `convertToOutputDTO` converte cada subclasse da entidade no DTO de saída correspondente. O service recusa atualizações que mudariam o tipo do produto.
- Os campos `id` dos DTOs de entrada têm `@Null`: o id vem pelo path, nunca pelo corpo.

### Convenções dos controllers
Todos os controllers seguem o mesmo modelo:
- `list` retorna 200, ou **204 com corpo vazio quando não há registros**.
- `detalhes` busca por id; `create` retorna 201; `update` atualiza por id.
- Cada endpoint tem anotações Swagger `@Operation`/`@ApiResponses`, e o corpo fica num try/catch que relança como `ResponseStatusException`.
- Os endpoints de exclusão existem, mas estão comentados.

Rotas começam em `/api/produto/...` e espelham o caminho do pacote. Segmentos com mais de uma palavra usam underscore (ex.: `medida_padrao`, `principio_ativo`).

## Arquitetura do frontend

- `src/App.jsx` concentra todo o roteamento. `MainLayout` renderiza a barra superior e o `ToastContainer`, e `ProdutoLayout` acrescenta a barra lateral do módulo de produto. As rotas do módulo ficam em `/produtos/*`. A gestão de preços fica em `/precos` (`Precos.jsx`), sem barra lateral.
- `src/pages/*.jsx` tem uma página autocontida por entidade. Cada página faz as requisições com a instância compartilhada do axios (`src/services/api.js`) e dá retorno ao usuário com `toast`. O estilo é quase todo inline (`style={{...}}`), além de classes globais em `App.css`/`index.css`.
- `CadastroProduto.jsx` monta o `tipoProduto` (`"remedio"`, `"generico"` ou `"geral"`) que o DTO polimórfico do backend espera. Ele envia produtos gerais para `/api/produto/geral`, rota que o backend não define: o `ProdutoController` aceita todos os tipos em `POST /api/produto`.