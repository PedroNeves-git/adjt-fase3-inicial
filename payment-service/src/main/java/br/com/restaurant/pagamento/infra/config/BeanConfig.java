package br.com.restaurant.pagamento.infra.config;

import org.springframework.context.annotation.Configuration;

// Os use cases recebem as portas via @RequiredArgsConstructor + @Component.
// O Spring resolve automaticamente porque PagamentoPendenteRepositoryAdapter,
// ProcpagHttpAdapter e PagamentoEventAdapter são @Component que implementam
// as respectivas portas de saída.
//
// Declare @Bean explícitos aqui se o Spring não resolver o wiring automático
// (especialmente em caso de dependência circular com ProcpagHttpAdapter).
@Configuration
public class BeanConfig {
}
