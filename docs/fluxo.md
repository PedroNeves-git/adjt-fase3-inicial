# Sistema de Autenticação e Processamento de Pedidos

Este repositório contém a documentação arquitetural do fluxo de autenticação de usuários e processamento assíncrono de pedidos utilizando **Microserviços**, **JWT (JSON Web Tokens)** e **Apache Kafka** como mensageria para comunicação orientada a eventos.

## 📌 Visão Geral do Fluxo

O ecossistema é composto por três serviços especializados:
1. **Auth-User**: Responsável pela autenticação, geração de tokens e gerenciamento de usuários (`authdb`).
2. **Order-Service**: Responsável pela criação de pedidos e gerenciamento de estados de compra (`orderdb`).
3. **Payment-Service**: Responsável por consumir intenções de pagamento, processar as transações e notificar o resultado.

---

## 🗺️ Diagrama de Arquitetura (Mermaid)

Abaixo está a representação visual atualizada do fluxo de dados. O GitHub renderiza este código nativamente em um diagrama interativo.

```mermaid
graph TD
    %% Definição de Estilos para o Tema Escuro/Profissional
    classDef clientStyle fill:#2d3748,stroke:#4a5568,stroke-width:2px,color:#fff,rx:5px,ry:5px;
    classDef serviceStyle fill:#1a202c,stroke:#718096,stroke-width:2px,color:#fff,rx:5px,ry:5px;
    classDef dbStyle fill:#2d3748,stroke:#4a5568,stroke-width:2px,color:#fff;
    classDef kafkaStyle fill:#2d3748,stroke:#e2e8f0,stroke-width:2px,color:#fff;

    %% Nós do Fluxo
    CLIENT[CLIENT]:::clientStyle
    
    subgraph Autenticação
        AUTH_SERVICE[Auth-User]:::serviceStyle
        AUTH_DB[(authdb)]:::dbStyle
    end

    subgraph Domínio de Negócio
        ORDER_SERVICE[Order-Service]:::serviceStyle
        ORDER_DB[(orderdb)]:::dbStyle
        KAFKA(((KAFKA))):::kafkaStyle
        PAYMENT_SERVICE[Payment-Service]:::serviceStyle
    end

    %% Relacionamentos - Autenticação
    CLIENT -->|POST / LOGIN| AUTH_SERVICE
    AUTH_SERVICE --> AUTH_DB
    AUTH_SERVICE -.->|JWT| CLIENT

    %% Relacionamentos - Criação de Pedido
    CLIENT -->|POST /orders <br> + JWT| ORDER_SERVICE
    ORDER_SERVICE --> ORDER_DB

    %% Relacionamentos - Mensageria (Kafka)
    ORDER_SERVICE -->|pagamento.criado| KAFKA
    KAFKA -->|pagamento.criado| PAYMENT_SERVICE
    
    PAYMENT_SERVICE -->|pagamento.aprovado| KAFKA
    KAFKA -->|pagamento.aprovado| ORDER_SERVICE

    %% Customização de Links
    linkStyle default stroke:#a0aec0,stroke-width:2px;