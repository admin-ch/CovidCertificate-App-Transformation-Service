/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.transformation.ws.config;

import ch.admin.bag.covidcertificate.backend.transformation.data.RateLimitDataService;
import ch.admin.bag.covidcertificate.backend.transformation.data.impl.JdbcRateLimitDataServiceImpl;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.BitClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.CertLightClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.PdfClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.RenewalClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.VerificationCheckClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.deserializer.CustomCovidCertificateDeserializer;
import ch.admin.bag.covidcertificate.backend.transformation.ws.config.model.PdfConfig;
import ch.admin.bag.covidcertificate.backend.transformation.ws.controller.TransformationController;
import ch.admin.bag.covidcertificate.backend.transformation.ws.service.RateLimitService;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.RestTemplateHelper;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.CovidCertificate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import java.time.ZoneId;
import java.util.List;
import javax.sql.DataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

@Configuration
public abstract class WsBaseConfig {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired private Environment env;

    @Value("${verification.check.baseurl}")
    private String verificationCheckBaseUrl;

    @Value("${verification.check.endpoint}")
    private String verificationCheckEndpoint;

    @Value("${verification.check.renewal.endpoint}")
    private String verificationCheckRenewalEndpoint;

    @Value("${verification.zone-id:default}")
    private String namedZoneId;

    @Value("${ws.rate-limit:10}")
    private int rateLimit;

    public abstract Flyway flyway(DataSource dataSource);

    public abstract BitClient bitClient(
            ClientRegistrationRepository clientRegistration, ObjectMapper objectMapper);

    @Bean
    public TransformationController transformationController(
            VerificationCheckClient verificationCheckClient,
            CertLightClient certLightClient,
            RateLimitService rateLimitService,
            PdfClient pdfClient,
            PdfConfig pdfConfig,
            RenewalClient renewalClient,
            boolean debug) {
        return new TransformationController(
                verificationCheckClient,
                certLightClient,
                rateLimitService,
                pdfClient,
                pdfConfig,
                renewalClient,
                debug);
    }

    @Bean
    public RenewalClient renewalClient(BitClient bitClient) {
        return new RenewalClient(bitClient);
    }

    @Bean
    public PdfClient pdfClient(PdfConfig pdfConfig, BitClient bitClient) {
        return new PdfClient(pdfConfig, bitClient);
    }

    @Bean
    public VerificationCheckClient verificationCheckClient(
            RestTemplate rt, ObjectMapper objectMapper) {
        return new VerificationCheckClient(
                verificationCheckBaseUrl,
                verificationCheckEndpoint,
                verificationCheckRenewalEndpoint,
                rt,
                objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper =
                new ObjectMapper()
                        .registerModule(new KotlinModule())
                        .registerModule(new JavaTimeModule())
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        var deserialization = new SimpleModule();
        deserialization.addDeserializer(
                CovidCertificate.class, new CustomCovidCertificateDeserializer());
        objectMapper.registerModule(deserialization);
        return objectMapper;
    }

    @Bean
    public CertLightClient certLightClient(ZoneId verificationZoneId, BitClient bitClient) {
        return new CertLightClient(verificationZoneId, bitClient);
    }

    @Bean
    public RateLimitDataService rateLimitDataService(DataSource dataSource) {
        return new JdbcRateLimitDataServiceImpl(dataSource);
    }

    @Bean
    public RateLimitService rateLimitService(RateLimitDataService rateLimitDataService) {
        return new RateLimitService(rateLimitDataService, rateLimit);
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withTableName("t_shedlock")
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build());
    }

    @Bean
    public boolean debug() {
        boolean debug = List.of(env.getActiveProfiles()).contains("debug");
        if (debug) {
            logger.info("debug profile is active");
        }
        return debug;
    }

    @Bean
    public ZoneId verificationZoneId() {
        try {
            return ZoneId.of(namedZoneId);
        } catch (Exception ex) {
            return ZoneId.systemDefault();
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return RestTemplateHelper.getRestTemplate();
    }
}
