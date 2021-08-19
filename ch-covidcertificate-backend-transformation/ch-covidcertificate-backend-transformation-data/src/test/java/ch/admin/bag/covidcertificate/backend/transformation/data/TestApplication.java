package ch.admin.bag.covidcertificate.backend.transformation.data;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {
            "ch.admin.bag.covidcertificate.backend.transformation.data",
            "ch.admin.bag.covidcertificate.log",
            "ch.admin.bag.covidcertificate.rest"
        },
        exclude = {SecurityAutoConfiguration.class})
public class TestApplication {}
