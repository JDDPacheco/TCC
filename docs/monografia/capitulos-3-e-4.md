# 3 IMPLEMENTAÇÃO DA SOLUÇÃO

Este capítulo descreve o desenvolvimento do Dr.StoreSys. A seção 3.1 apresenta os requisitos funcionais, os requisitos não funcionais e as regras de negócio do sistema. A seção 3.2 reúne a modelagem, por meio dos diagramas de casos de uso, de classes e de entidade-relacionamento. A seção 3.3 descreve a arquitetura adotada. A seção 3.4 apresenta os módulos implementados e as respectivas telas. Por fim, a seção 3.5 trata dos testes realizados para validar a solução.

## 3.1 REQUISITOS DO SISTEMA

Os requisitos foram definidos a partir da observação das rotinas de drogarias de pequeno porte, descrita na metodologia, e da regulamentação sanitária apresentada na seção 2.2. Em seguida, foram priorizados para compor o escopo do Produto Mínimo Viável (MVP), organizado nos quatro módulos do sistema: gestão de produtos, gestão de preços, gestão de estoque e gestão de vendas.

### 3.1.1 Requisitos funcionais

Os requisitos funcionais descrevem as funções que o sistema deve oferecer ao usuário. O Quadro 1 apresenta os requisitos funcionais implementados, agrupados por módulo.

**Quadro 1 – Requisitos funcionais**

| Código | Módulo | Descrição |
|---|---|---|
| RF01 | Produtos | Manter os cadastros de apoio dos medicamentos: laboratórios, princípios ativos, composições e fórmulas. |
| RF02 | Produtos | Cadastrar, consultar e alterar produtos, identificados pelo código de barras (EAN) e classificados como produto geral, medicamento ou medicamento genérico. |
| RF03 | Produtos | Registrar, para os medicamentos, a fórmula, o laboratório, a apresentação, a quantidade de doses e o tipo de controle de receita (isento, sob prescrição ou controle especial). |
| RF04 | Produtos | Exibir na tela inicial o catálogo de produtos, com nome comercial, marca, fórmula, saldo em estoque e preço atual, e permitir a busca por nome, marca, princípio ativo ou EAN. |
| RF05 | Preços | Definir o preço de venda de um produto, com início de vigência no momento do registro. |
| RF06 | Preços | Consultar o preço atual e o histórico de preços de cada produto. |
| RF07 | Estoque | Registrar a entrada de mercadoria por lote, com código do lote, validade, quantidade, custo unitário e nome do fornecedor. |
| RF08 | Estoque | Registrar ajustes de inventário a partir da quantidade contada, além de perdas e devoluções. |
| RF09 | Estoque | Consultar o saldo de cada produto, detalhado por lote e com a situação de validade de cada lote. |
| RF10 | Estoque | Emitir alertas de lotes vencidos, de lotes a vencer em um prazo informado e de produtos com saldo igual ou inferior ao estoque mínimo. |
| RF11 | Estoque | Consultar o histórico de movimentações de estoque por produto ou por lote. |
| RF12 | Vendas | Registrar vendas no ponto de venda com leitura do código de barras, aplicando o preço vigente e baixando o estoque automaticamente pelo critério FEFO. |
| RF13 | Vendas | Exigir a confirmação da receita para medicamentos sob prescrição e o registro dos dados da receita para medicamentos de controle especial. |
| RF14 | Vendas | Aplicar desconto em reais sobre o total da venda e receber o pagamento em dinheiro, cartão de débito, cartão de crédito ou PIX, com cálculo do troco no pagamento em dinheiro. |
| RF15 | Vendas | Cancelar uma venda, mediante motivo, devolvendo os itens aos lotes de origem. |
| RF16 | Vendas | Consultar as vendas de um período e o resumo de um dia, com os totais por forma de pagamento. |

Fonte: elaborado pelo autor (2026).

### 3.1.2 Requisitos não funcionais

Os requisitos não funcionais estabelecem as características de qualidade e as restrições técnicas do sistema. Eles estão apresentados no Quadro 2.

**Quadro 2 – Requisitos não funcionais**

| Código | Descrição |
|---|---|
| RNF01 | O sistema deve ser acessado por navegador web, sem instalação nas estações de trabalho. |
| RNF02 | O backend deve ser uma API REST independente da interface, que troca dados no formato JSON. |
| RNF03 | Os dados devem ser armazenados em um banco de dados relacional (MySQL). |
| RNF04 | O registro de uma venda e a baixa do estoque correspondente devem ocorrer em uma única transação: se qualquer etapa falhar, nada é gravado. |
| RNF05 | Vendas simultâneas não podem consumir o mesmo saldo de estoque. |
| RNF06 | Os dados recebidos devem ser validados no servidor, e os erros devem ser devolvidos com uma mensagem legível, exibida ao usuário na interface. |
| RNF07 | Os valores monetários devem ser tratados com precisão decimal, sem erros de arredondamento. |
| RNF08 | A API deve ter documentação interativa dos seus endpoints. |
| RNF09 | As regras de negócio devem ser cobertas por testes automatizados executáveis sem o banco de dados de produção. |

Fonte: elaborado pelo autor (2026).

### 3.1.3 Regras de negócio

As regras de negócio traduzem em restrições do sistema as exigências operacionais e sanitárias discutidas no Capítulo 2. O Quadro 3 apresenta as principais regras implementadas.

**Quadro 3 – Regras de negócio**

| Código | Regra |
|---|---|
| RN01 | O EAN é único: dois produtos não podem ter o mesmo código de barras. |
| RN02 | Um produto não pode mudar de tipo (geral, medicamento ou genérico) depois de cadastrado. |
| RN03 | Os preços não são sobrescritos: cada alteração gera um novo registro, e o preço atual é o de vigência mais recente. |
| RN04 | A entrada de medicamentos exige código do lote e validade. Produtos gerais usam um lote único, sem validade. |
| RN05 | Um lote é identificado pelo produto e pelo código do lote, pois laboratórios diferentes podem repetir códigos. Uma nova entrada em lote existente não pode informar outra validade. |
| RN06 | Não é permitida a entrada de mercadoria em lote vencido. |
| RN07 | Toda alteração de saldo gera uma movimentação de estoque, que não pode ser alterada nem excluída. |
| RN08 | Perdas e devoluções exigem a informação do motivo. |
| RN09 | Um lote é considerado vencido quando sua validade é anterior à data atual, e a vencer quando vence em até 30 dias. |
| RN10 | Na venda, o estoque é baixado pelo critério FEFO: os lotes vencidos são ignorados e os demais são consumidos em ordem de validade, podendo uma mesma venda utilizar mais de um lote. |
| RN11 | Não é possível vender produto sem preço cadastrado nem em quantidade superior ao saldo disponível. |
| RN12 | Medicamentos sob prescrição exigem a confirmação de que a receita foi apresentada. Medicamentos de controle especial exigem também nome do paciente, nome e registro do prescritor e número da receita. |
| RN13 | O desconto não pode ser maior que o valor bruto da venda e, no pagamento em dinheiro, o valor recebido não pode ser menor que o total. |
| RN14 | O preço cobrado em cada item é gravado no momento da venda e não muda se o preço do produto for alterado depois. |
| RN15 | Uma venda cancelada não é excluída: ela é marcada como cancelada, com data e motivo, e não pode ser cancelada novamente. |

Fonte: elaborado pelo autor (2026).

As regras RN04, RN06 e RN09 refletem as exigências da RDC nº 44/2009 de verificar lote e validade no recebimento e de não comercializar produtos vencidos (BRASIL, 2009b, art. 34 e art. 38). A regra RN12 corresponde à exigência de receita para medicamentos sob prescrição (BRASIL, 2009b, art. 43) e à identificação do paciente e do prescritor nas receitas de medicamentos sujeitos a controle especial (BRASIL, 1998).

## 3.2 MODELAGEM DO SISTEMA

A modelagem foi feita com diagramas da UML (*Unified Modeling Language*) e com o modelo entidade-relacionamento do banco de dados. Os diagramas desta seção foram elaborados a partir do código-fonte do sistema e, por isso, representam a solução efetivamente implementada.

### 3.2.1 Diagrama de casos de uso

A Figura 1 apresenta o diagrama de casos de uso do Dr.StoreSys. Como o protótipo ainda não possui controle de acesso, há um único ator, o funcionário da drogaria, que executa todas as funções do sistema. Os casos de uso estão agrupados pelos quatro módulos. O registro da venda (UC11) inclui a baixa do estoque pelo critério FEFO (UC13), que ocorre sempre, e pode ser estendido pela informação dos dados da receita (UC12), que só ocorre quando a venda contém medicamentos que a exigem.

**Figura 1 – Diagrama de casos de uso**

[INSERIR IMAGEM: docs/monografia/figuras/2-casos-de-uso.png]

Fonte: elaborado pelo autor (2026).

### 3.2.2 Diagrama de classes

A Figura 2 apresenta o diagrama de classes do domínio. A classe Produto representa os produtos de venda livre e é especializada pela classe Remedio, que acrescenta os dados próprios dos medicamentos, como fórmula, laboratório, apresentação e tipo de controle de receita. A classe Generico, por sua vez, especializa Remedio. A fórmula de um medicamento é composta por uma ou mais composições, e cada composição associa um princípio ativo à sua quantidade e unidade de medida, o que permite representar medicamentos com associação de princípios ativos.

O estoque é representado pela classe Lote, que guarda a validade e o saldo atual de cada lote de um produto, e pela classe MovimentacaoEstoque, que registra cada alteração desse saldo. A classe Preco guarda o histórico de preços de cada produto. A venda é representada pela classe Venda, composta por itens (ItemVenda). Cada item referencia o produto, o lote do qual a mercadoria saiu e o preço cobrado, além dos dados da receita (DadosReceita), quando exigidos.

**Figura 2 – Diagrama de classes do domínio**

[INSERIR IMAGEM: docs/monografia/figuras/3-classes.png]

Fonte: elaborado pelo autor (2026).

### 3.2.3 Modelo entidade-relacionamento

A Figura 3 apresenta o modelo entidade-relacionamento do banco de dados, gerado a partir do mapeamento objeto-relacional das classes. A hierarquia de produtos foi mapeada em uma única tabela (produto), em que a coluna tipo_produto identifica se o registro é um produto geral, um medicamento ou um genérico. Essa estratégia evita junções entre tabelas nas consultas de produtos, ao custo de manter colunas vazias para os produtos gerais. A relação muitos-para-muitos entre fórmulas e composições é implementada pela tabela formula_composicao. Os dados da receita são armazenados na própria tabela item_venda.

**Figura 3 – Modelo entidade-relacionamento**

[INSERIR IMAGEM: docs/monografia/figuras/4-der.png]

Fonte: elaborado pelo autor (2026).

## 3.3 ARQUITETURA DO SISTEMA

O Dr.StoreSys é composto por duas aplicações independentes: um backend, que expõe uma API REST, e um frontend do tipo Single Page Application (SPA), executado no navegador. A Figura 4 apresenta essa arquitetura.

**Figura 4 – Arquitetura do sistema**

[INSERIR IMAGEM: docs/monografia/figuras/1-arquitetura.png]

Fonte: elaborado pelo autor (2026).

O backend foi desenvolvido em Java 21 com o Spring Boot 3.4 e está organizado em camadas. A camada de controle (*controller*) recebe as requisições HTTP e converte os dados recebidos e enviados em objetos de transferência de dados (DTOs). A camada de serviço (*service*) concentra as regras de negócio e define os limites das transações. A camada de repositório (*repository*) acessa o banco de dados por meio do Spring Data JPA. Cada camada é dividida nos mesmos pacotes de domínio (produto, preço, estoque, venda e catálogo), o que facilita localizar o código de cada módulo.

Os produtos são identificados externamente pelo EAN, o mesmo código impresso no código de barras da embalagem, o que permite que a leitura do código de barras no caixa localize o produto diretamente. As rotas da API seguem o estilo REST descrito na seção 2.4.1, e os principais recursos estão resumidos no Quadro 4. A documentação completa dos endpoints é gerada automaticamente com o padrão OpenAPI e pode ser consultada pela interface Swagger UI.

**Quadro 4 – Principais recursos da API REST**

| Recurso | Operações principais |
|---|---|
| /api/produto | Cadastro, consulta e alteração de produtos; subrotas para laboratórios, princípios ativos, composições, fórmulas e tabelas de apoio. |
| /api/preco | Registro de preço, consulta do preço atual e do histórico de um produto. |
| /api/estoque | Saldos por produto e lote, entrada, ajuste, perda, devolução, estoque mínimo, alertas e movimentações. |
| /api/venda | Consulta de produto para o caixa, registro, consulta e cancelamento de vendas e resumo do dia. |
| /api/catalogo | Lista de produtos com marca, fórmula, saldo e preço atual, usada na tela inicial. |

Fonte: elaborado pelo autor (2026).

Quando uma regra de negócio é violada, o serviço interrompe a operação e a API responde com o código HTTP correspondente: 400 para dados inválidos, 404 para registros inexistentes e 409 para conflitos com o estado atual, como saldo insuficiente ou venda já cancelada. Em todos os casos, a resposta traz uma mensagem em português, que a interface exibe ao usuário em uma notificação.

O frontend foi desenvolvido em React 19 com o Vite. A navegação entre as telas é feita pela biblioteca react-router-dom, sem recarregar a página, e as requisições à API são feitas com a biblioteca axios. A busca de produtos por nome ou EAN foi implementada em um componente reutilizável, construído com a biblioteca react-select, que também aceita a leitura de leitores de código de barras, já que esses equipamentos funcionam como um teclado que digita o código e pressiona a tecla Enter.

## 3.4 MÓDULOS DESENVOLVIDOS

O desenvolvimento seguiu a ordem de dependência entre os módulos: primeiro o cadastro de produtos, depois os preços e o estoque e, por fim, as vendas, que utilizam os três anteriores. Esta seção descreve cada módulo e as telas correspondentes.

### 3.4.1 Gestão de produtos

O módulo de produtos permite cadastrar os três tipos de produto. Para os produtos gerais, como cosméticos e itens de higiene, são informados o nome comercial, o EAN e a unidade de venda. Para os medicamentos, o cadastro inclui ainda o laboratório, a fórmula, a apresentação (comprimido, xarope, pomada, entre outras), a quantidade de doses da embalagem e o tipo de controle de receita. As tabelas de apoio, como unidades de medida, apresentações e tipos de controle de receita, são preenchidas automaticamente na primeira execução do sistema.

A fórmula é montada a partir das composições cadastradas. Cada composição associa um princípio ativo a uma quantidade e a uma unidade de medida, como "dipirona monoidratada 500 mg", e uma fórmula pode reunir várias composições. Desse modo, medicamentos de marcas e laboratórios diferentes que possuem a mesma fórmula passam a compartilhar o mesmo registro, o que permite localizá-los pela busca por princípio ativo.

**Figura 5 – Tela de cadastro de produto**

[INSERIR PRINT: tela "Cadastro de Produto" com um medicamento preenchido]

Fonte: elaborado pelo autor (2026).

### 3.4.2 Gestão de preços

O módulo de preços registra o preço de venda de cada produto. Em vez de alterar o preço existente, cada novo valor é gravado como um novo registro, com a data e a hora de início da vigência. O preço atual é o registro mais recente, e os anteriores formam o histórico de preços do produto, que pode ser consultado na mesma tela. Essa decisão preserva a informação de quando e para quanto cada preço foi alterado, o que não seria possível se o valor fosse sobrescrito.

**Figura 6 – Tela de gestão de preços**

[INSERIR PRINT: tela "Preços" com o histórico de um produto]

Fonte: elaborado pelo autor (2026).

### 3.4.3 Gestão de estoque

O módulo de estoque controla o saldo de cada produto por lote. Na entrada de mercadoria, o usuário seleciona o produto e informa o código do lote, a validade, a quantidade recebida, o custo unitário e o nome do fornecedor. Para os produtos gerais, que não exigem esse controle, o sistema utiliza automaticamente um lote único, sem validade. O estoque é contado em unidades inteiras da unidade de venda do produto, sem fracionamento de embalagens.

Todas as alterações de saldo passam por um único método do serviço de estoque, que atualiza o lote e grava a movimentação correspondente na mesma transação. Cada movimentação registra o tipo (entrada, saída por venda, ajuste de inventário, perda, devolução ou cancelamento de venda), a quantidade, o saldo do lote após a operação, a data e a hora e uma observação. O histórico de movimentações permite, assim, reconstituir a origem do saldo de qualquer lote. No ajuste de inventário, o usuário informa a quantidade contada fisicamente, e o sistema calcula e registra a diferença em relação ao saldo anterior.

A consulta de estoque apresenta o saldo de cada produto e de cada um dos seus lotes, com a situação da validade: vencido, a vencer ou dentro da validade. A tela de alertas reúne os lotes vencidos, que devem ser retirados de venda, os lotes que vencem dentro de um prazo escolhido pelo usuário, inicialmente de 30 dias, e os produtos cujo saldo atingiu o estoque mínimo definido para eles. Esses alertas apoiam a política para produtos próximos ao vencimento e a retirada de produtos vencidos, previstas na RDC nº 44/2009 (BRASIL, 2009b, art. 38).

**Figura 7 – Tela de alertas de estoque**

[INSERIR PRINT: tela "Alertas" com lotes vencidos, a vencer e estoque baixo]

Fonte: elaborado pelo autor (2026).

### 3.4.4 Gestão de vendas

O módulo de vendas implementa o ponto de venda (PDV). No caixa, o funcionário lê o código de barras de cada produto ou o pesquisa pelo nome. O sistema consulta o preço atual, o saldo disponível e o tipo de controle de receita do produto e o acrescenta à venda. A leitura repetida de um mesmo produto aumenta a sua quantidade. Quando a venda contém um medicamento sob prescrição, a tela exige a confirmação de que a receita foi apresentada e, no caso de medicamentos de controle especial, solicita também o nome do paciente, o nome e o registro do prescritor e o número da receita. Em seguida, o funcionário informa o desconto, se houver, e a forma de pagamento. No pagamento em dinheiro, o sistema calcula o troco a partir do valor recebido.

A Figura 8 apresenta a sequência de operações do registro de uma venda. Antes de gravar qualquer informação, o serviço de vendas valida todos os itens: a existência do produto, o preço vigente, o saldo disponível, os dados da receita, o desconto e o pagamento. Só depois a venda é gravada e, para cada produto, o serviço de estoque realiza a baixa pelo critério FEFO. Para isso, ele bloqueia os lotes do produto que não estão vencidos, consome-os em ordem de validade e devolve os lotes utilizados e as respectivas quantidades. Quando a quantidade vendida é maior que o saldo do primeiro lote, a baixa continua no lote seguinte, e a venda passa a ter um item para cada lote utilizado. Assim, cada item de venda fica associado ao lote do qual a mercadoria efetivamente saiu.

**Figura 8 – Diagrama de sequência do registro de uma venda**

[INSERIR IMAGEM: docs/monografia/figuras/5-sequencia-venda.png]

Fonte: elaborado pelo autor (2026).

Todo esse processo ocorre em uma única transação. O bloqueio dos lotes impede que duas vendas simultâneas consumam o mesmo saldo: se outra venda esgotar o estoque entre a validação e a baixa, a transação inteira é desfeita e nenhuma informação é gravada. Os valores monetários são representados no backend pelo tipo BigDecimal, de precisão decimal, e os totais exibidos no caixa são calculados em centavos, com números inteiros, para evitar erros de arredondamento.

O cancelamento é feito sobre a venda inteira, com motivo obrigatório. A venda não é excluída, mas marcada como cancelada, com a data e o motivo do cancelamento, e cada item volta ao lote de onde saiu, por meio de uma movimentação do tipo cancelamento de venda. O módulo oferece ainda o histórico de vendas por período e o resumo do dia, que apresenta a quantidade e o valor das vendas concluídas e os totais por forma de pagamento, para apoiar o fechamento do caixa.

**Figura 9 – Tela do caixa (PDV)**

[INSERIR PRINT: tela "Caixa" com itens na venda, incluindo um medicamento que exige receita]

Fonte: elaborado pelo autor (2026).

### 3.4.5 Tela inicial e catálogo de produtos

A tela inicial reúne as informações mais consultadas no balcão. Ela apresenta todos os produtos cadastrados, com nome comercial, marca do laboratório, fórmula, saldo em estoque e preço atual, além de um atalho para o caixa. A busca não diferencia letras maiúsculas e minúsculas nem acentuação, e exige que cada palavra digitada apareça no nome, na marca, na fórmula ou no EAN do produto. Desse modo, a pesquisa por "dipirona" encontra tanto os produtos com esse nome quanto os que possuem esse princípio ativo em sua fórmula, o que facilita oferecer ao cliente alternativas de marcas diferentes ou o medicamento genérico correspondente.

Para que a tela seja carregada rapidamente mesmo com muitos produtos, os preços atuais e os saldos de todos os produtos são obtidos em poucas consultas ao banco de dados, em vez de uma consulta por produto.

**Figura 10 – Tela inicial com o catálogo de produtos**

[INSERIR PRINT: tela inicial com uma busca por princípio ativo]

Fonte: elaborado pelo autor (2026).

## 3.5 TESTES E VALIDAÇÃO

As regras de negócio foram verificadas por testes automatizados escritos com o JUnit e executados sobre um banco de dados em memória (H2). Foram utilizados dois tipos de teste. Os testes de serviço chamam diretamente os métodos da camada de serviço e verificam as regras de negócio, como a ordem de consumo dos lotes na baixa FEFO ou a recusa de uma venda sem receita. Os testes de API simulam requisições HTTP aos endpoints e verificam os códigos de resposta e as mensagens de erro. Cada teste é executado dentro de uma transação desfeita ao final, de modo que um teste não interfere nos demais. A Tabela 1 apresenta a quantidade de testes por módulo.

**Tabela 1 – Quantidade de testes automatizados por módulo**

| Módulo | Testes de serviço | Testes de API | Total |
|---|---:|---:|---:|
| Produtos | 16 | 17 | 33 |
| Preços | 5 | 6 | 11 |
| Estoque | 21 | 7 | 28 |
| Vendas | 20 | 8 | 28 |
| Catálogo | 3 | 1 | 4 |
| Inicialização da aplicação | – | – | 1 |
| **Total** | **65** | **39** | **105** |

Fonte: elaborado pelo autor (2026).

Os módulos de estoque e de vendas concentram a maior parte dos testes de serviço, por reunirem as regras mais críticas: a baixa por FEFO com divisão entre lotes e o descarte de lotes vencidos, a recusa de entradas em lotes vencidos, o cálculo do ajuste de inventário, as exigências de receita conforme o tipo de controle, os limites de desconto e de valor recebido, e o estorno ao lote de origem no cancelamento. A escrita dos testes dos módulos de produto e de preço, realizada depois da implementação desses módulos, revelou cinco defeitos, que foram corrigidos e passaram a ser verificados pelos próprios testes, de modo a impedir que voltem a ocorrer. Ao final do desenvolvimento, os 105 testes eram executados com sucesso.

Complementarmente, foram realizados testes funcionais pela interface do sistema, executando os fluxos completos de cadastro de produtos, entrada de mercadoria, venda e cancelamento de venda.

# 4 CONSIDERAÇÕES FINAIS

Este trabalho partiu da constatação de que drogarias de pequeno porte costumam registrar vendas, controlar o estoque e definir preços com métodos manuais, planilhas ou sistemas de ponto de venda genéricos, o que gera falhas operacionais em um segmento cujo estoque, embora pequeno, é complexo e regulado pela Anvisa. Diante disso, definiu-se como objetivo geral analisar e desenvolver o Dr.StoreSys, um protótipo de sistema web integrado de gestão para essas drogarias, reunindo em uma única aplicação o cadastro de produtos, a precificação, o controle de estoque por lote e validade e o registro de vendas no ponto de venda.

Os objetivos específicos foram atendidos ao longo do desenvolvimento. Os requisitos funcionais, não funcionais e as regras de negócio foram levantados e organizados em quatro módulos. A estrutura do sistema foi modelada por meio dos diagramas de casos de uso, de classes, de entidade-relacionamento e de sequência. Foram desenvolvidas uma API REST em Java, com Spring Boot e MySQL, e uma interface web em React. O cadastro de produtos passou a diferenciar produtos gerais, medicamentos e genéricos, com fórmula, laboratório e tipo de controle de receita, e os preços passaram a ter histórico. O estoque passou a ser controlado por lote e validade, com entrada de mercadoria, ajustes, perdas, alertas e baixa automática pelo critério FEFO. O ponto de venda passou a aplicar o preço vigente, verificar as exigências de receita e permitir o cancelamento com estorno ao estoque. Por fim, a solução foi validada por 105 testes automatizados e por testes funcionais pela interface.

O principal resultado do trabalho é a integração entre vendas, estoque e preços. No Dr.StoreSys, a venda não é apenas registrada: no mesmo momento em que ela é concluída, o sistema aplica o preço vigente, confere o saldo, exige a receita quando necessária e baixa o estoque do lote que vence primeiro, registrando de qual lote cada unidade saiu. Essas verificações, que em um PDV genérico dependem da atenção do atendente, passam a ser garantidas pelo sistema, o que responde ao problema que motivou o trabalho.

Como contribuição prática, o trabalho apresenta um protótipo funcional voltado especificamente à realidade das drogarias de pequeno porte, que traduz exigências da regulamentação sanitária, como a verificação de lote e validade e o controle da receita, em regras de negócio verificadas por testes automatizados. Como contribuição acadêmica, documenta o processo de levantamento de requisitos, modelagem e implementação de um sistema desse tipo, que pode servir de referência para outros trabalhos sobre sistemas de gestão para o varejo farmacêutico.

O protótipo possui limitações que devem ser consideradas. O sistema ainda não possui autenticação nem perfis de acesso, de modo que qualquer pessoa com acesso à rede pode executar todas as funções, e as operações não são associadas ao funcionário que as realizou. Não há integração com o Sistema Nacional de Gerenciamento de Produtos Controlados (SNGPC), nem emissão de documento fiscal. O cadastro de fornecedores e de notas fiscais de entrada não foi implementado, e o custo de compra registrado nas entradas ainda não é utilizado no cálculo da margem de lucro. Cada venda aceita uma única forma de pagamento, e o estoque não permite a venda fracionada de embalagens. Além disso, o sistema foi validado por testes automatizados e funcionais realizados pelo próprio autor, e não foi utilizado na rotina de uma drogaria real.

Como trabalhos futuros, sugere-se: implementar a autenticação de usuários, com perfis distintos para atendentes e farmacêuticos e o registro do responsável por cada operação; integrar o sistema ao SNGPC e à emissão de documento fiscal eletrônico; cadastrar fornecedores e notas fiscais de entrada e utilizar o custo de compra no cálculo da margem e na sugestão de preços; permitir mais de uma forma de pagamento na mesma venda; e, principalmente, implantar o protótipo em drogarias de pequeno porte para avaliar sua usabilidade e seu impacto na rotina desses estabelecimentos. O Dr.StoreSys configura-se, assim, como uma base inicial sobre a qual um sistema de gestão completo para pequenas drogarias pode ser construído.
