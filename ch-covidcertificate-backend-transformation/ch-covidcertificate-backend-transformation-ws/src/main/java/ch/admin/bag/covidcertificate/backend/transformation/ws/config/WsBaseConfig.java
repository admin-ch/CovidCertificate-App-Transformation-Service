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
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.TokenReceiver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class WsBaseConfig {

    @Value("${mock.url:test}")
    private String mockUrl;

    @Value("${ws.jwt.client-secret:}")
    private String clientSecret;
    @Value("${ws.jwt.client-id:}")
    private String clientId;
    @Value("${ws.jwt.scope:openid}")
    private String scope;
    @Value("${ws.jwt.token-endpoint:}")
    private String tokenEndpoint;

    @Value("${ws.jwt.grant-type:client_credentials}")
    private String grantType;

    @Bean
    public TransformationController transformationController(MockHelper mockHelper, TokenReceiver tokenReceiver) {
        return new TransformationController(mockHelper, tokenReceiver);
    }

    @Bean
    public TokenReceiver tokenReceiver() {
        return new TokenReceiver(tokenEndpoint, grantType, scope, clientId, clientSecret);
    }

    @Bean
    public MockHelper mockHelper() {
        return new MockHelper(mockUrl);
    }
}
