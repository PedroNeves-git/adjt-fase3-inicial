# 🗺️ Documentação de Fluxos de Sistema (Dark Mode)

Este documento centraliza as especificações técnicas e o mapeamento visual dos fluxos de pagamento, resiliência de serviços e mensageria distribuída.

---

## 1. Ciclo de Vida do Pedido & Resiliência (Circuit Breaker)

Este fluxo ilustra o acionamento do serviço de pagamento a partir da criação de um pedido, detalhando a estratégia de reentratividade (*retry*) e o comportamento de contingência caso o circuito esteja aberto.

### Diagrama de Fluxo

```mermaid
graph TD
    %% Configuração de Estilos (Paleta Dark & Clean)
    classDef default fill:#111827,stroke:#374151,stroke-width:1px,color:#94a3b8;
    classDef process fill:#1f2937,stroke:#4b5563,stroke-width:2px,color:#f8fafc;
    classDef decision fill:#312e81,stroke:#4338ca,stroke-width:2px,color:#e0e7ff;
    classDef success fill:#064e3b,stroke:#059669,stroke-width:2px,color:#a7f3d0;
    classDef alert fill:#7c2d12,stroke:#ea580c,stroke-width:2px,color:#ffedd5;

    %% Nós do Fluxo
    A[order-service<br>cria pedido]:::process --> B[pedido.criado]:::process
    B --> C[payment-service]:::process
    C --> D[procpag]:::process
    D --> E{sucesso?}:::decision
    
    %% Ramificação de Sucesso
    E -- Sim --> F[pagamento.aprovado]:::success
    F --> G[order-service<br>atualiza]:::process
    
    %% Ramificação de Falha / Resiliência
    E -- Não --> H{circuit breaker<br>aberto?}:::decision
    H -- Não --> I[retry]:::alert
    I --> D
    
    H -- Sim --> J[pagamento.pendente]:::alert
