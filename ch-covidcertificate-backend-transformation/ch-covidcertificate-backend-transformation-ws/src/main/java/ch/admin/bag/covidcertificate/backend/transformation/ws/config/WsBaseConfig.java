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
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.CertLightClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.VerificationCheckClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.controller.TransformationController;
import ch.admin.bag.covidcertificate.backend.transformation.ws.service.RateLimitService;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;
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

@Configuration
public abstract class WsBaseConfig {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired private Environment env;

    @Value("${mock.url:test}")
    private String mockUrl;

    @Value("${transform.light.endpoint}")
    private String lightCertificateEnpoint;

    @Value("${ws.jwt.client-id:default-client}")
    private String clientId;

    @Value("${verification.check.baseurl}")
    private String verificationCheckBaseUrl;

    @Value("${verification.check.endpoint}")
    private String verificationCheckEndpoint;

    @Value("${verification.zone-id:default}")
    private String namedZoneId;

    @Value("${ws.rate-limit:10}")
    private int rateLimit;

    public abstract DataSource dataSource();

    public abstract Flyway flyway();

    @Bean
    public TransformationController transformationController(
        VerificationCheckClient verificationCheckClient,
        CertLightClient certLightClient,
        RateLimitService rateLimitService,
        boolean debug) {
        return new TransformationController(
                verificationCheckClient,
                certLightClient,
            rateLimitService,
                debug);
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
    public OauthWebClient tokenReceiver(ClientRegistrationRepository clientRegistration) {
        return new OauthWebClient(clientId, clientRegistration);
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
    public RateLimitService rateLimitService(RateLimitDataService rateLimitDataService) {
        return new RateLimitService(rateLimitDataService, rateLimit);
    }

    @Bean
    public VerificationCheckClient verificationCheckClient() {
        return new VerificationCheckClient(verificationCheckBaseUrl, verificationCheckEndpoint);
    }

    @Bean
    public CertLightClient certLightClient(
            OauthWebClient oauthWebClient, ZoneId verificationZoneId) {
        return new CertLightClient(lightCertificateEnpoint, oauthWebClient, verificationZoneId);
    }

    @Bean
    public RateLimitDataService rateLimitDataService(DataSource dataSource) {
        return new JdbcRateLimitDataServiceImpl(dataSource);
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
}
