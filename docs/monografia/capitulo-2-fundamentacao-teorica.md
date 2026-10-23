# 2 FUNDAMENTAÇÃO TEÓRICA

Este capítulo apresenta os conceitos que embasam o desenvolvimento do Dr.StoreSys. A seção 2.1 trata da gestão de drogarias de pequeno porte, com ênfase na rotina do ponto de venda e no controle de estoque por lote e validade. A seção 2.2 reúne a regulamentação sanitária que incide sobre a comercialização de medicamentos e que orientou as regras de negócio do sistema. A seção 2.3 discute o papel dos sistemas de ponto de venda e da automação no varejo. Por fim, a seção 2.4 apresenta as tecnologias utilizadas na implementação da solução.

## 2.1 GESTÃO DE DROGARIAS DE PEQUENO PORTE

A Lei nº 5.991, de 17 de dezembro de 1973, que dispõe sobre o controle sanitário do comércio de medicamentos no Brasil, define drogaria como o "estabelecimento de dispensação e comércio de drogas, medicamentos, insumos farmacêuticos e correlatos em suas embalagens originais" (BRASIL, 1973, art. 4º, inciso XI). A mesma lei diferencia a drogaria da farmácia, que, além do comércio, realiza a manipulação de fórmulas magistrais e oficinais, e define dispensação como o "ato de fornecimento ao consumidor de drogas, medicamentos, insumos farmacêuticos e correlatos, a título remunerado ou não" (BRASIL, 1973, art. 4º, inciso XV). A drogaria, portanto, não produz o que vende: sua atividade consiste em adquirir produtos industrializados, armazená-los e entregá-los ao consumidor, o que coloca o registro das vendas e o controle do estoque no centro da sua operação.

Além dos medicamentos, as drogarias estão autorizadas a comercializar uma ampla variedade de produtos, como cosméticos, perfumes, produtos de higiene pessoal, suplementos e produtos médicos de uso domiciliar (BRASIL, 2009a). O estoque de uma drogaria reúne, assim, itens de naturezas distintas: produtos de venda livre, que exigem apenas o controle de quantidade e de preço, e medicamentos, que estão sujeitos a exigências sanitárias quanto à validade, à rastreabilidade por lote e, em muitos casos, à apresentação de receita. Nas drogarias de pequeno porte, essa complexidade convive com a ausência de Procedimentos Operacionais Padrão (ARAGÃO; MESQUITA; SOUSA, 2014), o que torna a rotina dependente da experiência de quem está no balcão.

Na rotina diária, o atendimento no ponto de venda envolve identificar o produto solicitado, informar o preço e a disponibilidade, verificar se o produto exige receita, registrar a venda e receber o pagamento. Na retaguarda, a drogaria recebe mercadorias, confere os lotes e as validades, ajusta o estoque após contagens e retira de circulação os produtos vencidos ou avariados. Cada uma dessas atividades altera ou consulta o estoque, de modo que um registro incompleto em qualquer etapa compromete as informações utilizadas nas demais.

### 2.1.1 Controle de estoque por lote e validade

Diferentemente de outros segmentos do varejo, em que o estoque pode ser controlado apenas pela quantidade de cada produto, os medicamentos são identificados pelo lote de fabricação e possuem prazo de validade. A Resolução da Diretoria Colegiada (RDC) nº 44/2009 da Agência Nacional de Vigilância Sanitária (Anvisa) determina que, no recebimento, o estabelecimento verifique, entre outros itens, o lote e a validade dos produtos (BRASIL, 2009b, art. 34). A mesma norma estabelece que os produtos com prazo de validade expirado devem ser segregados e não podem ser comercializados, e que o estabelecimento deve definir, em seu Manual de Boas Práticas, uma política para os produtos próximos ao vencimento (BRASIL, 2009b, art. 38). Por esse motivo, o saldo de um medicamento precisa ser conhecido por lote, e não apenas pelo total do produto.

Para organizar a saída dos produtos perecíveis, uma das práticas utilizadas é o critério FEFO (*First Expired, First Out*), em que o primeiro produto a vencer é o primeiro a sair. Ele difere do critério FIFO (*First In, First Out*), que considera a ordem de entrada da mercadoria, porque um lote recebido depois pode vencer antes de outro já armazenado. Em um levantamento realizado com 50 farmácias de pequeno porte na província de Cavite, nas Filipinas, Soliveres, Herrera e Cedillo (2024) identificaram o FEFO entre os métodos de armazenamento adotados por esses estabelecimentos e constataram que ele é frequentemente aplicado nas vendas. Samsul, L e Sugiarti (2023), por sua vez, desenvolveram um sistema web de monitoramento do estoque de suprimentos de saúde baseado no FEFO, no qual os itens mais próximos do vencimento são utilizados primeiro e um recurso de notificação alerta sobre a proximidade da validade.

Quando o FEFO é aplicado manualmente, cabe ao atendente escolher, no momento da venda, a embalagem do lote que vence primeiro, o que depende de atenção e de uma organização física rigorosa das prateleiras. Em um sistema informatizado, a escolha do lote pode ser feita automaticamente no registro da venda, a partir das validades registradas na entrada da mercadoria, e os lotes próximos do vencimento podem ser apresentados em alertas, o que apoia a política exigida pela regulamentação.

## 2.2 REGULAMENTAÇÃO SANITÁRIA

A comercialização de medicamentos no Brasil é regulada pela Anvisa. A RDC nº 44, de 17 de agosto de 2009, dispõe sobre as Boas Práticas Farmacêuticas para o controle sanitário do funcionamento, da dispensação e da comercialização de produtos e da prestação de serviços farmacêuticos em farmácias e drogarias, e estabelece os critérios e condições mínimas para o cumprimento dessas boas práticas (BRASIL, 2009b, art. 1º). Entre as exigências relacionadas à dispensação, a norma determina que, antes da entrega do medicamento, seja realizada a inspeção visual do produto, verificando, no mínimo, o prazo de validade e a integridade da embalagem (BRASIL, 2009b, art. 46). Determina também que o usuário seja alertado quando o prazo de validade do medicamento estiver próximo do vencimento, e veda a dispensação quando a posologia prescrita não puder ser concluída dentro desse prazo (BRASIL, 2009b, art. 51).

### 2.2.1 Medicamentos sob prescrição

Os medicamentos sujeitos à prescrição somente podem ser dispensados mediante a apresentação da respectiva receita (BRASIL, 2009b, art. 43). Ao recebê-la, o farmacêutico deve avaliá-la quanto à legibilidade e à ausência de rasuras, à identificação do usuário, à identificação do medicamento, sua concentração, dosagem, forma farmacêutica e quantidade, ao modo de usar ou posologia, à duração do tratamento e à identificação do prescritor, com o número de registro no respectivo conselho profissional (BRASIL, 2009b, art. 44).

Os antimicrobianos possuem regras próprias, definidas pela RDC nº 471, de 23 de fevereiro de 2021. A receita desses medicamentos é válida por 10 dias a contar da data de sua emissão (BRASIL, 2021, art. 7º), e a dispensação é feita mediante a retenção da segunda via da receita, sendo a primeira via devolvida ao paciente (BRASIL, 2021, art. 10).

### 2.2.2 Medicamentos sujeitos a controle especial

As substâncias e os medicamentos sujeitos a controle especial, como os entorpecentes, os psicotrópicos e outras substâncias de uso controlado, são regulamentados pela Portaria SVS/MS nº 344, de 12 de maio de 1998. Para as substâncias das listas A1, A2, A3, B1, B2, C2 e C3, a dispensação exige a Notificação de Receita (BRASIL, 1998, art. 35), documento que, além dos dados do prescritor e do medicamento, contém a identificação do comprador, como nome, documento de identidade, endereço e telefone. Para os medicamentos da lista C1, a prescrição é feita em Receita de Controle Especial, preenchida em duas vias: a primeira fica retida na farmácia ou drogaria e a segunda é devolvida ao paciente como orientação, sendo a receita válida por 30 dias a partir da data de emissão (BRASIL, 1998, art. 52).

Em todos esses casos, a regulamentação exige que a drogaria verifique a receita antes da venda e, nos medicamentos controlados, que guarde os dados da prescrição e do comprador. Em um sistema de ponto de venda genérico, que não distingue os medicamentos pelo tipo de controle, essa verificação depende inteiramente do atendente. Ao associar a cada medicamento o tipo de controle de receita, um sistema voltado para drogarias pode exigir a confirmação da receita no momento da venda e registrar, junto com ela, os dados da prescrição.

## 2.3 SISTEMAS DE PONTO DE VENDA E AUTOMAÇÃO NO VAREJO

A automação de processos é apontada como um ponto-chave da transformação digital no varejo, considerada essencial para a inovação e a competitividade dos estabelecimentos (LAZAREVA et al., 2022). Nesse contexto, o sistema de Ponto de Venda (PDV) ocupa uma posição central, por ser o local em que cada venda é registrada. Handayani e Prasetyo (2020) destacam que o PDV é um elemento básico da gestão de vendas e que ele é crucial também para o controle de estoque e de preços, já que é no momento da venda que o estoque é baixado e que o preço vigente é aplicado.

No segmento farmacêutico, Parkan (2003) mostrou que a introdução de sistemas eletrônicos de PDV em drogarias resultou em melhorias significativas no desempenho operacional, e que os sistemas manuais são menos eficientes e dificultam o controle de vendas e de estoque. Ainda assim, os pequenos varejistas frequentemente combinam soluções já existentes, como sistemas de PDV genéricos, sites de busca, planilhas e aplicativos móveis, para evitar a complexidade e os custos das soluções desenvolvidas para grandes redes (ISHARYANI et al., 2023). Essa combinação de ferramentas, no entanto, mantém separadas as informações de vendas, de estoque e de preços, e a falta de um sistema informatizado que automatize essas atividades leva a falhas operacionais e compromete a tomada de decisões gerenciais (SNEHA, 2025).

Um PDV integrado à gestão de estoque e de preços permite que, ao ler o código de barras do produto, o sistema apresente o preço vigente e o saldo disponível, verifique as exigências de receita e, ao concluir a venda, dê baixa no estoque do lote correto, sem que o atendente precise consultar outras ferramentas. É essa integração entre vendas, estoque e preços, aliada às exigências sanitárias descritas na seção 2.2, que diferencia um sistema voltado para drogarias de um PDV genérico.

## 2.4 TECNOLOGIAS UTILIZADAS

Esta seção apresenta a arquitetura adotada e as tecnologias empregadas no desenvolvimento do Dr.StoreSys.

### 2.4.1 Arquitetura REST e Single Page Application

O sistema foi construído em duas aplicações separadas, que se comunicam por meio de uma API (*Application Programming Interface*) no estilo REST (*Representational State Transfer*). Esse estilo arquitetural foi proposto por Fielding (2000) para sistemas distribuídos de hipermídia, como a Web, e é definido por um conjunto de restrições: a separação entre cliente e servidor; a comunicação sem estado, em que cada requisição contém todas as informações necessárias para ser compreendida; o uso de cache; uma interface uniforme para acessar os recursos; e a organização do sistema em camadas. Em uma API REST, cada recurso, como um produto ou uma venda, é identificado por um endereço, e as operações sobre ele são realizadas por meio dos métodos do protocolo HTTP.

A separação entre cliente e servidor permite que a interface do usuário seja implementada como uma Single Page Application (SPA), uma aplicação carregada uma única vez no navegador, que atualiza o conteúdo da tela a partir dos dados obtidos do servidor, sem recarregar a página a cada interação (MIKOWSKI; POWELL, 2013). No Dr.StoreSys, o servidor concentra as regras de negócio e o acesso ao banco de dados, enquanto a SPA é responsável pela apresentação das telas e pela interação com o usuário.

### 2.4.2 Java, Spring Boot e MySQL

O backend foi desenvolvido na linguagem Java, uma linguagem orientada a objetos amplamente utilizada no desenvolvimento de aplicações corporativas (DEITEL; DEITEL, 2016). Sobre ela, foi utilizado o Spring Boot, que, segundo sua documentação, ajuda a criar aplicações independentes, baseadas no framework Spring e prontas para produção, que podem ser executadas diretamente (SPRING, 2026). O Spring Boot reduz a configuração necessária para iniciar um projeto e reúne, em módulos, recursos como o servidor web embutido, a validação dos dados recebidos e o acesso a banco de dados.

A persistência dos dados foi feita no sistema gerenciador de banco de dados relacional MySQL [CITAR FONTE: documentação do MySQL], por meio do Spring Data JPA, que realiza o mapeamento entre as classes do sistema e as tabelas do banco de dados e dispensa a escrita manual da maior parte das consultas SQL. A documentação da API foi gerada com o padrão OpenAPI, por meio da biblioteca springdoc, que disponibiliza uma interface (Swagger UI) para consultar e testar os endpoints.

### 2.4.3 React e Vite

O frontend foi desenvolvido com o React, definido por seus mantenedores como uma biblioteca para a construção de interfaces de usuário a partir de partes individuais chamadas componentes (REACT.DEV, 2026). Cada tela do sistema é composta por componentes que podem ser reutilizados em diferentes telas, como o campo de busca de produtos, utilizado tanto na entrada de mercadoria quanto no caixa. Como ferramenta de desenvolvimento e de geração da versão final do frontend, foi utilizado o Vite, ferramenta de build voltada para projetos web modernos, que oferece um servidor de desenvolvimento com atualização imediata das alterações (VITE, 2026).

### 2.4.4 Testes automatizados com JUnit

Para verificar as regras de negócio, foram escritos testes automatizados com o JUnit, framework de testes para a plataforma Java (JUNIT, 2026). Os testes automatizados executam o código do sistema com dados de exemplo e comparam o resultado obtido com o resultado esperado, o que permite repetir a verificação de todo o sistema a cada alteração e detectar rapidamente quando uma mudança quebra uma funcionalidade que já estava correta.

---

# REFERÊNCIAS (novas, do capítulo 2)

> Inserir em ordem alfabética junto com as referências já existentes do anteprojeto (LAZAREVA et al., HANDAYANI; PRASETYO, ARAGÃO; MESQUITA; SOUSA, SNEHA, ISHARYANI et al., PARKAN, GIL, MIKOWSKI; POWELL, DEITEL; DEITEL, REACT.DEV). A referência da IN nº 9/2009, que já existe, passa a ser **BRASIL, 2009a**.

BRASIL. Lei nº 5.991, de 17 de dezembro de 1973. Dispõe sobre o Controle Sanitário do Comércio de Drogas, Medicamentos, Insumos Farmacêuticos e Correlatos, e dá outras Providências. **Diário Oficial da União**, Brasília, DF, 19 dez. 1973. Disponível em: https://www.planalto.gov.br/ccivil_03/leis/l5991.htm. Acesso em: 22 out. 2026.

BRASIL. Ministério da Saúde. Secretaria de Vigilância Sanitária. Portaria nº 344, de 12 de maio de 1998. Aprova o Regulamento Técnico sobre substâncias e medicamentos sujeitos a controle especial. **Diário Oficial da União**, Brasília, DF, [CONFERIR DATA DE PUBLICAÇÃO]. Disponível em: https://bvsms.saude.gov.br/bvs/saudelegis/svs/1998/prt0344_12_05_1998_rep.html. Acesso em: 22 out. 2026.

BRASIL. Ministério da Saúde. Agência Nacional de Vigilância Sanitária. Resolução da Diretoria Colegiada – RDC nº 44, de 17 de agosto de 2009. Dispõe sobre Boas Práticas Farmacêuticas para o controle sanitário do funcionamento, da dispensação e da comercialização de produtos e da prestação de serviços farmacêuticos em farmácias e drogarias e dá outras providências. **Diário Oficial da União**, Brasília, DF, 18 ago. 2009b. Disponível em: https://bvsms.saude.gov.br/bvs/saudelegis/anvisa/2009/rdc0044_17_08_2009.pdf. Acesso em: 22 out. 2026.

BRASIL. Ministério da Saúde. Agência Nacional de Vigilância Sanitária. Resolução da Diretoria Colegiada – RDC nº 471, de 23 de fevereiro de 2021. Dispõe sobre os critérios para a prescrição, dispensação, controle, embalagem e rotulagem de medicamentos à base de substâncias classificadas como antimicrobianos de uso sob prescrição, isoladas ou em associação, listadas em Instrução Normativa específica. **Diário Oficial da União**, Brasília, DF, 24 fev. 2021. Acesso em: 22 out. 2026.

FIELDING, Roy Thomas. **Architectural styles and the design of network-based software architectures**. 2000. Tese (Doutorado em Information and Computer Science) – University of California, Irvine, 2000. Disponível em: https://ics.uci.edu/~fielding/pubs/dissertation/top.htm. Acesso em: 22 out. 2026.

JUNIT. **JUnit 5 User Guide**. 2026. Disponível em: https://junit.org/junit5/docs/current/user-guide/. Acesso em: 22 out. 2026.

REACT.DEV. **React**: the library for web and native user interfaces. 2026. Disponível em: https://react.dev/. Acesso em: 22 out. 2026.

SAMSUL, S. W.; L, H.; SUGIARTI. Design and implementation of health supplies inventory monitoring system using first expired first out method. **Indonesian Journal of Data and Science**, v. 4, n. 2, p. 113-123, 2023. DOI: https://doi.org/10.56705/ijodas.v4i2.72.

SOLIVERES, V. L.; HERRERA, A.; CEDILLO, A. K. Inventory management practices of small-scale pharmacies in the selected towns in Cavite: a marketing perspective. **Logistic and Operation Management Research**, v. 3, n. 1, p. 38-52, 2024. DOI: https://doi.org/10.31098/lomr.v3i1.2194.

SPRING. **Spring Boot**. 2026. Disponível em: https://docs.spring.io/spring-boot/index.html. Acesso em: 22 out. 2026.

VITE. **Getting Started**. 2026. Disponível em: https://vite.dev/guide/. Acesso em: 22 out. 2026.
