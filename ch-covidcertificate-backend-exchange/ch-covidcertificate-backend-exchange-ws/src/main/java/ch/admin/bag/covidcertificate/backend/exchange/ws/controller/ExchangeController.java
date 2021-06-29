package ch.admin.bag.covidcertificate.backend.exchange.ws.controller;

import ch.ubique.openapi.docannotations.Documentation;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/exchange/v1/")
public class ExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);

    @PostConstruct
    public void init() {
        logger.info("Started controller!");
    }

    @Documentation(
            description = "Echo endpoint",
            responses = {"200 => Hello from CH CovidCertificate Exchange WS"})
    @GetMapping("")
    public @ResponseBody String hello() {
        return "Hello from CH CovidCertificate Exchange WS";
    }
}
