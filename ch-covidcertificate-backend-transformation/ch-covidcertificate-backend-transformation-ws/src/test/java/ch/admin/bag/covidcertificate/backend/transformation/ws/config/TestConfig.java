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
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.BitClientMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Profile("test")
@Configuration
public class TestConfig extends WsBaseConfig {

    @Autowired DataSource dataSource;

    public DataSource dataSource() {
        return dataSource;
    }

    @Bean
    @Override
    public Flyway flyway(DataSource dataSource) {
        final var flyway =
                Flyway.configure()
                        .dataSource(dataSource)
                        .locations("classpath:/db/migration/pgsql")
                        .validateOnMigrate(true)
                        .load();
        flyway.migrate();
        return flyway;
    }

    @Bean
    public ClientRegistrationRepository clientRegistration() {
        return new ClientRegistrationRepository() {
            @Override
            public ClientRegistration findByRegistrationId(String s) {
                return null;
            }
        };
    }

    @Bean
    @Override
    public BitClient bitClient(
            ClientRegistrationRepository clientRegistration, ObjectMapper objectMapper) {
        return new BitClientMock(objectMapper);
    }
}
