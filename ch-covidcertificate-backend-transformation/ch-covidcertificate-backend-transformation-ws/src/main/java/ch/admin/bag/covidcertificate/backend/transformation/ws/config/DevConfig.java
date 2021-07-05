package ch.admin.bag.covidcertificate.backend.transformation.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class DevConfig extends WsBaseConfig {}
