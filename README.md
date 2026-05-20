# 🍽️ Restaurant Online Orders — Tech Challenge Fase 3

<div align="center">
 <h2> Sumário</h2>
  <a href="#descrição-do-projeto">Descrição do projeto</a> -
  <a href="#arquitetura">Arquitetura</a> -
  <a href="#funcionalidades">Funcionalidades</a> -
  <a href="#ferramentas-utilizadas">Ferramentas utilizadas</a> -
  <a href="#guia-de-implantação">Guia de implantação</a> -
  <a href="#urls-importantes">URLs importantes</a> -
  <a href="#desenvolvedores">Desenvolvedores</a>
</div>

## Descrição do projeto

<p align="justify">
Este projeto foi criado para a terceira fase do Tech Challenge da pós-graduação em Desenvolvimento e Arquitetura Java da instituição FIAP.

A aplicação consiste em um sistema distribuído para pedidos online em um restaurante, composto por múltiplos microsserviços que se comunicam de forma síncrona (via REST) e assíncrona (via Kafka). O fluxo cobre desde o cadastro e autenticação do cliente até o processamento resiliente do pagamento, com integração a um serviço externo eventualmente disponível.

A arquitetura segue os princípios de Clean Architecture em cada serviço, com separação clara entre camadas (core e infra), enquanto a comunicação entre serviços é desacoplada por eventos. Padrões de resiliência (Circuit Breaker, Retry, Timeout e Fallback) são aplicados na integração com o serviço externo de pagamentos, garantindo tolerância a falhas.
</p>

## Arquitetura

O sistema é composto por três serviços de aplicação, um serviço externo fornecido, um broker de mensageria e bancos isolados por serviço.

```
[Cliente]
   │
   ▼
[auth-service]  ──── emite JWT ────► usado em todos os endpoints protegidos
   │
   ▼
[order-service] ──── publica ────► (Kafka: pedido.criado)
                                            │
                                            ▼
                                  [payment-service]
                                            │ Resilience4j
                                            ▼
                                       [procpag]
                                            │
              ┌─────────────────────────────┴─────┐
              ▼                                   ▼
   (Kafka: pagamento.aprovado)          (Kafka: pagamento.pendente)
              │                                   │
              ▼                                   ▼
       [order-service]                    [payment-service worker]
       atualiza para PAID                 reprocessa pendência
```

| Serviço | Função |
|---|---|
| **auth-service** | Cadastro e autenticação de clientes. Emite JWT (HS256) com claims `userId` e `userRole`. |
| **order-service** | Criação e consulta de pedidos. Publica `pedido.criado` no Kafka e consome eventos de pagamento. |
| **payment-service** | Consome `pedido.criado`, chama o `procpag` com Resilience4j e publica o resultado. Possui DLT para mensagens com falha persistente. |
| **procpag** | Serviço externo de pagamentos (fornecido), eventualmente disponível. |
| **Kafka + ZooKeeper** | Mensageria assíncrona entre os serviços. |
| **MySQL (×2)** | Banco dedicado para `auth-service` e `order-service`. |

## Funcionalidades

`Funcionalidade 1:` Cadastro e autenticação de clientes com JWT.

`Funcionalidade 2:` Catálogo de itens do menu pré-carregado.

`Funcionalidade 3:` Criação de pedidos vinculados ao cliente autenticado (ID extraído do token).

`Funcionalidade 4:` Confirmação de pedido com publicação assíncrona do evento `pedido.criado`.

`Funcionalidade 5:` Consulta de pedido por ID e listagem de pedidos do cliente logado.

`Funcionalidade 6:` Processamento de pagamento com Resilience4j (Circuit Breaker, Retry, Timeout, Fallback).

`Funcionalidade 7:` Atualização automática do status do pedido a partir de eventos Kafka.

`Funcionalidade 8:` Tratamento de indisponibilidade do serviço externo com marcação de pedidos como `PENDENTE_PAGAMENTO` e reprocessamento.

## Ferramentas utilizadas
<div style="display: flex; gap: 15px">
<a href="https://www.java.com" target="_blank">
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="Java" width="40" height="40"/>
</a>

<a href="https://spring.io/" target="_blank">
    <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg" alt="Spring" width="40" height="40"/>
</a>

<a href="https://kafka.apache.org/" target="_blank">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/apachekafka/apachekafka-original.svg" alt="Kafka" width="40" height="40"/>
</a>

<a href="https://www.mysql.com/" target="_blank">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/mysql/mysql-original.svg" alt="MySQL" width="40" height="40"/>
</a>

<a href="https://www.docker.com/" target="_blank">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/docker/docker-plain.svg" alt="Docker" width="40" height="40"/>
</a>

<a href="https://www.postman.com/" target="_blank">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postman/postman-original.svg" alt="Postman" width="40"/>
</a>

<a href="https://maven.apache.org/" target="_blank">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/apache/apache-original.svg" alt="Maven" width="40" height="40"/>
</a>
</div>

**Stack detalhada:** Java 17 · Spring Boot 3.5 · Spring Security · Spring Kafka · Spring Data JPA · Resilience4j · jjwt 0.12 · Springdoc OpenAPI · Lombok · MySQL 8 · Apache Kafka 7.6 · Docker Compose · Maven.

## Guia de implantação

Antes de iniciar o projeto, certifique-se de ter o [Docker](https://www.docker.com/) e o [Git](https://git-scm.com/) instalados.

Clone o repositório:

```bash
git clone https://github.com/PedroNeves-git/adjt-fase3-inicial.git
cd adjt-fase3-inicial
```

Suba toda a stack em segundo plano:

```bash
docker compose up -d --build
```

Aguarde alguns segundos e verifique se todos os containers estão saudáveis:

```bash
docker compose ps
```

Para parar e remover os containers (mantendo os volumes):

```bash
docker compose down
```

Para parar e apagar **também os dados** dos bancos:

```bash
docker compose down -v
```

## URLs importantes

| Recurso | URL |
|---|---|
| Swagger UI — auth-service | http://localhost:8081/api-users/v1/swagger-ui/index.html |
| Swagger UI — order-service | http://localhost:8082/api-orders/v1/swagger-ui/index.html |
| OpenAPI spec — procpag | http://localhost:8089/openapi.yml |
| Health — order-service | http://localhost:8082/api-orders/v1/actuator/health |
| Health — payment-service | http://localhost:8083/actuator/health |
| Circuit Breakers (Resilience4j) | http://localhost:8083/actuator/circuitbreakers |
| Retries (Resilience4j) | http://localhost:8083/actuator/retries |

### Portas expostas

| Serviço | Porta |
|---|---|
| auth-service | 8081 |
| order-service | 8082 |
| payment-service | 8083 |
| procpag | 8089 |
| MySQL (auth-db) | 3307 |
| MySQL (order-db) | 3308 |
| Kafka (host) | 29092 (externo) / 9092 (interno docker) |
| ZooKeeper | 2181 |

### Tópicos Kafka

| Tópico | Publica | Consome |
|---|---|---|
| `pedido.criado` | order-service | payment-service |
| `pagamento.aprovado` | payment-service | order-service |
| `pagamento.pendente` | payment-service | order-service, payment-service (worker) |

## Testando a API

Uma collection Postman completa está disponível na pasta de documentação do projeto. Ela cobre:

- Health checks dos três serviços
- Fluxo de autenticação (registro + login) com captura automática do token
- Listagem do catálogo
- Ciclo completo de pedido (criação → confirmação → consulta de status atualizado)
- Monitoramento dos Circuit Breakers e Retries do Resilience4j
- Cenários de erro (sem token, payload inválido, recurso inexistente)

Importe o arquivo `restaurant-phase3.postman_collection.json` no Postman para começar.

## Desenvolvedores
<table align="center">
  <tr>
    <td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/PedroNeves-git" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Pedro Neves   </b><br>
            <a href="https://www.linkedin.com/in/pedro-neves-867001258/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/PedroNeves-git" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>
    <td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/breenoox" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Breno Barbosa   </b><br>
            <a href="https://www.linkedin.com/in/brenobarbosa22/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/breenoox" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>
    <td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/GuiFonsCode" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Guilherme Fonseca   </b><br>
            <a href="https://www.linkedin.com/in/guifonseca1212/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/GuiFonsCode" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>
  </tr>
</table>
