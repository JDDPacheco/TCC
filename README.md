# Dr.StoreSys

Repositório do meu TCC do curso de Análise e Desenvolvimento de Sistemas do IFAM.

O Dr.StoreSys é um protótipo de sistema integrado de gestão para drogarias de pequeno porte. Ele é voltado à operação diária do ponto de venda: registro de vendas, controle de estoque e precificação. O sistema é composto por uma API REST (backend) e uma interface web (frontend).

## Situação dos módulos

| Módulo | Situação |
| --- | --- |
| Gestão de Produto | Concluído: laboratórios, princípios ativos, composições, fórmulas, cadastro e consulta de produtos (geral, remédio e genérico) |
| Gestão de Preço | Concluído: registro do preço de venda por produto, consulta do preço atual e histórico de preços |
| Gestão de Venda | Em construção |
| Gestão de Estoque | Em construção |

## Tecnologias

- **Backend**: Java 21, Spring Boot 3.4 (Web, Data JPA, Security), MySQL e springdoc-openapi (Swagger)
- **Frontend**: React 19, Vite, React Router, Axios, React Select e React Toastify

## Estrutura do repositório

```
backend/    API REST em Spring Boot
frontend/   Interface web em React
docs/       Anteprojeto, monografia do TCC (em desenvolvimento) e material de referência
dbconfig/   Consultas SQL de apoio para inspecionar o banco
```

## Como executar

### Pré-requisitos

- Java 21
- Node.js e npm
- MySQL rodando em `localhost`

### Backend

1. Ajuste o usuário e a senha do MySQL em `backend/src/main/resources/application.properties`. O banco `drStoreSys` é criado automaticamente, e as tabelas são geradas a partir das entidades.
2. Na pasta `backend/`, execute:

   ```
   ./mvnw spring-boot:run
   ```

   No Windows, use `mvnw.cmd spring-boot:run`.

A API sobe em `http://localhost:8080`. Na primeira execução, as tabelas de apoio (unidades de medida, apresentações, controle de receita etc.) são populadas automaticamente.

A documentação interativa da API fica em http://localhost:8080/swagger-ui.html.

### Frontend

1. Por padrão, o frontend acessa o backend em `http://localhost:8080`. Para usar outro endereço (ex.: o backend em outra máquina da rede), crie o arquivo `frontend/.env.local` com:

   ```
   VITE_API_URL=http://<endereço-do-backend>:8080
   ```
2. Na pasta `frontend/`, execute:

   ```
   npm install
   npm run dev
   ```

A interface fica disponível em `http://localhost:5173`.

### Testes e lint

```
cd backend && ./mvnw test     # requer o MySQL rodando
cd frontend && npm run lint
```
