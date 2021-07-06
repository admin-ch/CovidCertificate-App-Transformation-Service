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

import ch.admin.bag.covidcertificate.backend.transformation.ws.controller.TransformationController;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.MockHelper;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public abstract class WsBaseConfig {

    @Value("${mock.url:test}")
    private String mockUrl;

    @Value("${ws.jwt.client-id:default-client}")
    private String clientId;

    @Bean
    public TransformationController transformationController(MockHelper mockHelper, OauthWebClient tokenReceiver) {
        return new TransformationController(mockHelper, tokenReceiver);
    }

    @Bean
    public OauthWebClient tokenReceiver(ClientRegistrationRepository clientRegistration) {
        return new OauthWebClient(clientId, clientRegistration);
    }

    @Bean
    public MockHelper mockHelper() {
        return new MockHelper(mockUrl);
    }
}
