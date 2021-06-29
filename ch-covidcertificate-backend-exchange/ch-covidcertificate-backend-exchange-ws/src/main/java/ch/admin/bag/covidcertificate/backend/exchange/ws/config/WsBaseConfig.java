package ch.admin.bag.covidcertificate.backend.exchange.ws.config;

import ch.admin.bag.covidcertificate.backend.exchange.ws.controller.ExchangeController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class WsBaseConfig {

    @Bean
    public ExchangeController exchangeController() {
        return new ExchangeController();
    }
}
