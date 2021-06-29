package ch.admin.bag.covidcertificate.backend.exchange.ws.config;

import ch.admin.bag.covidcertificate.backend.exchange.ws.controller.ExchangeController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfig {

    @Bean
    public ExchangeController exchangeController() {
        return new ExchangeController();
    }
}
