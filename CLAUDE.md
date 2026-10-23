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

### Documentos (branch `monografia`)
Os documentos do TCC ficam na pasta `docs/` da branch **`monografia`**, e não na branch da aplicação. Para consultá-los sem trocar de branch: `git show monografia:docs/<arquivo>` (ou `git worktree add ../TCC-monografia monografia`).
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
- **Situação da monografia**: a monografia é escrita no Google Docs. Os textos dos capítulos 2, 3 e 4 e os diagramas do sistema (gerados a partir do código, com fontes em PlantUML) ficam em `docs/monografia/`, na branch `monografia`.

Para requisitos, modelo de dados e regras de negócio já implementados, a fonte da verdade é o código deste repositório. Para o que o sistema deve cobrir, a referência é a nossa monografia.

São duas aplicações independentes, sem build na raiz:
- `backend/`: API REST em Spring Boot 3.4, Java 21, Maven, MySQL e springdoc/Swagger.
- `frontend/`: React 19 + Vite em JavaScript puro (JSX), com axios, react-router-dom, react-select e react-toastify.
- `dbconfig/testes.sql`: SELECTs avulsos para inspecionar tabelas. Não é schema nem migration.

## Comandos

Backend (executar em `backend/`; no Windows use `mvnw.cmd`):
```
./mvnw spring-boot:run                 # sobe a API na porta 8080
./mvnw test                            # roda os testes (H2 em memória; não precisa do MySQL)
./mvnw test -Dtest=EstoqueServiceTest  # uma classe de teste (acrescente #metodo para um único método)
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

- **Banco de dados**: MySQL em `localhost`, schema `drStoreSys` (criado automaticamente). As credenciais ficam em `backend/src/main/resources/application.properties`. Com `ddl-auto=update` não há migrations: o schema é gerado a partir das entidades JPA.
- **`ddl-auto=update` não remove colunas nem restrições**: ao renomear ou apagar um campo de uma entidade, a coluna antiga continua no banco. Se ela for `NOT NULL`, os inserts passam a falhar com "Field '...' doesn't have a default value" (aconteceu com `preco`, que tinha `data_vigencia`, `preco_custo` e `is_ativo` da versão antiga). O mesmo vale para restrições: um banco criado antes do módulo de estoque mantém em `lote` o `UNIQUE` antigo só em `codigo_lote` e o `data_validade NOT NULL`, além da tabela `estoque`, que não é mais usada. Nesses casos, ajuste ou apague a coluna/tabela à mão.
- **Dados iniciais**: `config/DataLoader.java` (um `CommandLineRunner`) popula as tabelas de apoio (controle de receita, medidas básicas/farmacêuticas/padrão, apresentações) e cria um exemplo de princípio ativo, composição, fórmula e laboratório. Cada tabela só é populada se estiver vazia. Novos valores de apoio devem ser adicionados ali.
- **MySQL 8.4+ / Docker**: a URL do banco já inclui `allowPublicKeyRetrieval=true`, necessário para a autenticação `caching_sha2_password` com `useSSL=false`. Sem MySQL instalado, dá para subir um container: `docker run -d --name drstoresys-mysql -e MYSQL_ROOT_PASSWORD=<senha do application.properties> -p 3306:3306 mysql:8.4`.
- **URL do backend no frontend**: `frontend/src/services/api.js` lê `VITE_API_URL` e, se ela não existir, usa `http://localhost:8080`. Para apontar para outra máquina (ex.: um IP da rede local), crie `frontend/.env.local` (ignorado pelo git) com `VITE_API_URL=http://<ip>:8080`. O CORS do backend (`config/CorsConfig.java`) libera qualquer origem (`*`).
- **Segurança** (`config/SecurityConfig.java`): CSRF desativado e todas as requisições liberadas (`permitAll`). Ainda não há autenticação.
- **Validação**: o `spring-boot-starter-validation` está ativo, então as anotações `jakarta.validation` dos DTOs com `@Valid` são aplicadas. O payload do frontend precisa trazer todos os campos obrigatórios. Exemplo: o `ProdutoDTO` exige `ean` também no `PUT`, mesmo o produto sendo localizado pelo EAN da URL. Não use `@NotBlank` em campos que não sejam texto: isso gera erro 500. Para números, use `@NotNull`/`@Positive`.
- **Mensagens de erro**: `server.error.include-message=always` faz a mensagem das `ResponseStatusException` chegar no campo `message` do corpo. `config/TratadorDeErros.java` converte erros de validação e JSON mal formado em 400 com mensagem legível, no mesmo formato. No frontend, use `mensagemDeErro(error, textoPadrao)` de `src/utils/formatacao.js`. Os controllers antigos (try/catch genérico) também passam a expor a mensagem da exceção original.
- **Testar a API com acentos pelo terminal**: no Git Bash do Windows, o `curl -d '...'` não envia acentos em UTF-8 e o backend responde "corpo mal formatado". Grave o JSON num arquivo e use `--data-binary @arquivo.json`.
- **Testes**: `backend/src/test/resources/application.properties` substitui o de produção e usa H2 em memória (modo MySQL, `create-drop`). O `DataLoader` roda nos testes e popula as tabelas de apoio. Os testes de service/controller usam `@SpringBootTest` + `@Transactional`, então cada teste é desfeito ao final. No MockMvc, o corpo JSON de uma `ResponseStatusException` não é gerado (ele vem da página `/error`); confira a mensagem com `status().reason(...)`. O frontend não tem testes.
  - Há testes de service (`service/<módulo>/*ServiceTest`) e de HTTP (`controller/<módulo>/*ControllerTest`) para os quatro módulos (produto, preço, estoque e venda) e para o catálogo.
  - **Bugs conhecidos** ficam registrados como testes com `@Disabled("Bug conhecido: ...")`, que descrevem o comportamento correto (hoje não há nenhum). Ao corrigir um bug, remova o `@Disabled` e deixe o teste como regressão. Para rodar testes desativados: `./mvnw test -Dtest=<Classe> "-DargLine=-Djunit.jupiter.conditions.deactivate=org.junit.*DisabledCondition"`.

## Arquitetura do backend

Pacote raiz: `com.josepacheco.tcc`. As camadas são `controller → service → repository → model`, com classes `dto` entre controller e service. Todas as camadas repetem os mesmos subpacotes de domínio: `produto`, `produto/remedio/atributos`, `produto/remedio/formulacao`, `preco`, `estoque`, `venda` e `catalogo` (este só com service, controller e DTO de leitura).

Os quatro módulos previstos no escopo (produto, preço, estoque e venda) estão implementados.

### Módulo de estoque
- **Lote**: o saldo fica no próprio `Lote` (`quantidadeAtual`); não existe mais a entidade `Estoque`. A unicidade é **produto + `codigoLote`**, porque laboratórios diferentes podem repetir códigos.
  - **Remédios** (incluindo genéricos) exigem código do lote e validade na entrada.
  - **Produtos gerais** usam um **lote único** automático (`EstoqueService.CODIGO_LOTE_UNICO = "UNICO"`, validade nula). Código e validade enviados são ignorados.
  - O estoque é contado em **unidades inteiras** da `MedidaPadrao` do produto. Não há fracionamento.
- **`MovimentacaoEstoque`**: registro imutável de cada alteração de saldo, com tipo (`TipoMovimentacao`: `ENTRADA`, `SAIDA_VENDA`, `AJUSTE_INVENTARIO`, `PERDA`, `DEVOLUCAO`, `CANCELAMENTO_VENDA`), quantidade com sinal, saldo após, data/hora e observação. Nas entradas, guarda também `custoUnitario` e `fornecedor` (só o nome; não há cadastro de fornecedores nem nota fiscal).
  - Toda mudança de saldo passa por `EstoqueService.movimentar(...)`, que atualiza o lote e grava a movimentação na mesma transação. Não altere `quantidadeAtual` por fora dele.
- **Regras**:
  - não se dá entrada em lote vencido;
  - reentrada num lote existente com outra validade é recusada;
  - o ajuste recebe a **quantidade contada** e registra a diferença;
  - perda e devolução exigem motivo;
  - "vencido" = validade anterior a hoje; "a vencer" = vence em até `DIAS_ALERTA_VENCIMENTO` (30) dias;
  - o estoque mínimo é o campo opcional `Produto.estoqueMinimo`, e o produto entra no alerta quando o saldo disponível fica ≤ mínimo.
- **Baixa por venda (FEFO)**: `EstoqueService.baixarEstoque(ean, quantidade, observacao)` consome primeiro o lote que vence antes, ignora os vencidos e pode dividir a baixa entre lotes. Ela trava os lotes com `PESSIMISTIC_WRITE` e devolve `List<LoteBaixado>` (lote + quantidade), usada pelo `VendaService` para preencher `ItemVenda.lote`. Não tem endpoint próprio: é chamada dentro da transação da venda. O estorno de venda cancelada usa `estornarBaixa(lote, quantidade, observacao)`.

### Módulo de venda
- **Modelo**:
  - `Venda`: `valorBruto`, `desconto` (R$ sobre o total), `valorTotal`, `formaPagamento` (`DINHEIRO`, `CARTAO_DEBITO`, `CARTAO_CREDITO`, `PIX`; uma por venda), `valorRecebido`/`troco` (só em dinheiro), `status` (`CONCLUIDA`/`CANCELADA`) e dados do cancelamento.
  - `Venda.itens` usa `mappedBy = "venda"`; a chave estrangeira fica em `item_venda`.
  - `ItemVenda` guarda produto, **lote**, quantidade e o **preço cobrado no momento**, que não muda se o preço do produto for alterado depois.
  - Como a baixa FEFO pode usar vários lotes, um produto pode gerar **vários `ItemVenda`**, um por lote, com os mesmos dados de receita.
- **Registro (`VendaService.registrarVenda`)**, numa transação única:
  1. valida **tudo antes de gravar**: produto, preço atual (sem preço = 409), saldo disponível (409), receita, desconto ≤ valor bruto e, em dinheiro, valor recebido ≥ total;
  2. salva a venda, para gerar o número usado na observação das movimentações ("Venda nº X");
  3. chama `baixarEstoque` para cada produto. A baixa revalida o saldo com os lotes travados; se uma venda concorrente esgotar o estoque nesse meio-tempo, a transação inteira é desfeita.

  Um mesmo EAN não pode aparecer duas vezes na venda (400).
- **Receita** (pelo `ControleReceita.tipoControle` do remédio):
  - `sob_prescricao` exige `receitaApresentada = true`;
  - `especial` exige também paciente, prescritor, registro do prescritor e número da receita (`DadosReceita`, `@Embeddable` em `item_venda`);
  - `isento` e produtos gerais ignoram os dados de receita enviados;
  - não há integração com o SNGPC.
- **Cancelamento**: a venda inteira, com motivo obrigatório. Ela é marcada `CANCELADA` (nunca apagada) e cada item volta ao lote de origem como `CANCELAMENTO_VENDA`. Cancelar de novo dá 409.
- **Rotas** em `/api/venda`:
  - `GET /produto/{ean}` (preço atual, saldo e exigência de receita, para o caixa);
  - `POST /`;
  - `GET /?inicio=&fim=` (padrão: hoje; 204 se vazio);
  - `GET /{id}`;
  - `POST /{id}/cancelamento`;
  - `GET /resumo?data=` (fechamento do dia, só com as concluídas; inclui todas as formas de pagamento, mesmo sem vendas).
- O `VendaController` segue o estilo do `EstoqueController`.

### Catálogo (tela inicial)
- `GET /api/catalogo` (`CatalogoService`) devolve todos os produtos em ordem de nome, com marca do laboratório, fórmula (composições unidas por " + "), tipo de controle, saldo disponível e preço atual (204 se não há produtos).
- Os preços atuais vêm de uma única consulta (`PrecoRepository.findPrecosAtuais`) e os saldos de `EstoqueService.listarSaldos()`, para não fazer uma consulta por produto.
- A busca da tela inicial é feita no frontend.
- **Rotas** em `/api/estoque`:
  - `GET /` (saldos de todos os produtos) e `GET /{ean}` (saldo e lotes);
  - `PUT /{ean}/estoque_minimo`;
  - `POST /entrada`, `/ajuste`, `/perda` e `/devolucao`;
  - `GET /alertas/vencimento?dias=`, `/alertas/vencidos` e `/alertas/estoque_baixo`;
  - `GET /movimentacoes?ean=&idLote=`.
- O `EstoqueController` segue o estilo do `PrecoController`: sem try/catch, com os erros do service propagados (400/404/409). Tem as anotações Swagger e o 204 em listas vazias dos controllers de produto.
- `SaldoProdutoOutputDTO`/`EstoqueService` usam `Hibernate.unproxy(produto)` antes de `instanceof Remedio`/`Generico`, porque o produto obtido via `Lote` pode ser um proxy do Hibernate.

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
- Cada endpoint tem anotações Swagger `@Operation`/`@ApiResponses`, e o corpo fica num try/catch. Um `catch (ResponseStatusException e) { throw e; }` vem **antes** do `catch (Exception e)` genérico, para que os erros de regra de negócio do service (404, 409, 400) cheguem ao cliente com o status original. Mantenha essa ordem ao criar endpoints nesse estilo.
- Nos services, a busca por id usa `findById(...).orElseThrow(404)` (método `buscarPorId`). Não use `getReferenceById`: ele não falha para id inexistente, e o erro só aparece depois, como 500.
- Os endpoints de exclusão existem, mas estão comentados.

Rotas começam em `/api/produto/...` e espelham o caminho do pacote. Segmentos com mais de uma palavra usam underscore (ex.: `medida_padrao`, `principio_ativo`).

## Arquitetura do frontend

- `src/App.jsx` concentra todo o roteamento. `MainLayout` renderiza a barra superior e o `ToastContainer`; o nome "💊 Dr.StoreSys" na barra (classe `marca-sistema`) é o link para a tela inicial, e não existe item "Início" no menu.
- A tela inicial (`Inicio.jsx`, rota `/`) tem:
  - um atalho para o Caixa (PDV);
  - a tabela do catálogo (nome comercial, marca, fórmula, estoque e preço atual);
  - uma busca que ignora maiúsculas e acentos (`normalizarTexto`) e exige que cada palavra digitada apareça no nome, na marca, na fórmula ou no EAN.
- `ProdutoLayout` acrescenta a barra lateral do módulo de produto. As rotas do módulo ficam em `/produtos/*`. A gestão de preços fica em `/precos` (`Precos.jsx`), sem barra lateral. O módulo de estoque tem o próprio layout (`EstoqueLayout`) em `/estoque/*`, com as telas `EntradaEstoque`, `ConsultaEstoque`, `AjustesEstoque`, `AlertasEstoque` e `MovimentacoesEstoque`. O de vendas (`VendaLayout`) fica em `/vendas/*`, com `CaixaVenda` (PDV), `HistoricoVendas` e `ResumoVendas`.
- No `CaixaVenda`, o `SeletorProduto` recebe `ean={null}`, então o campo volta a ficar vazio depois de cada leitura e o mesmo produto lido de novo soma 1 à quantidade. Os totais são calculados em centavos (inteiros), para evitar erros de ponto flutuante. Para a data de hoje, use `dataDeHoje()` de `formatacao.js`, nunca `toISOString()`, que usa UTC.
- Peças compartilhadas (até agora usadas pelo estoque):
  - `src/components/SeletorProduto.jsx`: busca de produto por nome ou EAN com react-select; também funciona com leitor de código de barras;
  - `src/components/EtiquetaSituacao.jsx`: situação de validade do lote;
  - `src/utils/formatacao.js`: moeda, datas e `mensagemDeErro`;
  - `src/utils/estilos.js`: estilos inline comuns.
- Datas `LocalDate` (`aaaa-mm-dd`) devem ser formatadas pelo texto (`formatarData`), nunca com `new Date(...)`: essa conversão interpreta a data como UTC e, no fuso de Manaus, mostra o dia anterior.
- `src/pages/*.jsx` tem uma página autocontida por entidade. Cada página faz as requisições com a instância compartilhada do axios (`src/services/api.js`) e dá retorno ao usuário com `toast`. O estilo é quase todo inline (`style={{...}}`), além de classes globais em `App.css`/`index.css`.
- `CadastroProduto.jsx` monta o `tipoProduto` (`"remedio"`, `"generico"` ou `"geral"`) que o DTO polimórfico do backend espera. Ele envia produtos gerais para `/api/produto/geral`, rota que o backend não define: o `ProdutoController` aceita todos os tipos em `POST /api/produto`.