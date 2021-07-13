package ch.admin.bag.covidcertificate.backend.transformation.data;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {"ch.admin.bag.covidcertificate.backend.transformation.data"},
        exclude = {SecurityAutoConfiguration.class})
public class TestApplication {}
