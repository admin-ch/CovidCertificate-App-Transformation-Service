package ch.admin.bag.covidcertificate.backend.exchange.ws.config.controller;

import ch.ubique.openapi.docannotations.Documentation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("exchange/v1/")
public class ExchangeController {

    @Documentation(
            description = "Echo endpoint",
            responses = {"200 => Hello from CH CovidCertificate Exchange WS"})
    @GetMapping("")
    public @ResponseBody String hello() {
        return "Hello from CH CovidCertificate Exchange WS";
    }


}
