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

import ch.admin.bag.covidcertificate.backend.transformation.ws.client.BitClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.BitClientOauthImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.PooledServiceConnectorConfig.PoolConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@Profile("cloud")
public class WsCloudConfig extends WsBaseConfig {

    @Value("${datasource.maximumPoolSize:5}")
    int dataSourceMaximumPoolSize;

    @Value("${datasource.connectionTimeout:30000}")
    int dataSourceConnectionTimeout;

    @Value("${datasource.leakDetectionThreshold:0}")
    int dataSourceLeakDetectionThreshold;

    @Value("${transform.light.endpoint}")
    private String lightCertificateEndpoint;

    @Value("${transform.renew.endpoint}")
    private String certRenewalEndpoint;

    @Value("${ws.jwt.client-id:default-client}")
    private String clientId;

    @Bean
    @Override
    public DataSource dataSource() {
        PoolConfig poolConfig =
                new PoolConfig(dataSourceMaximumPoolSize, dataSourceConnectionTimeout);
        DataSourceConfig dbConfig =
                new DataSourceConfig(
                        poolConfig,
                        null,
                        null,
                        Map.of("leakDetectionThreshold", dataSourceLeakDetectionThreshold));
        CloudFactory factory = new CloudFactory();
        return factory.getCloud().getSingletonServiceConnector(DataSource.class, dbConfig);
    }

    @Bean
    @Override
    public Flyway flyway() {
        Flyway flyWay =
                Flyway.configure()
                        .dataSource(dataSource())
                        .locations("classpath:/db/migration/pgsql_cluster")
                        .load();
        flyWay.migrate();
        return flyWay;
    }

    @Bean
    @Override
    public BitClient bitClient(
            ClientRegistrationRepository clientRegistration, ObjectMapper objectMapper) {
        return new BitClientOauthImpl(
                clientId,
                clientRegistration,
                lightCertificateEndpoint,
                objectMapper,
                certRenewalEndpoint);
    }
}
