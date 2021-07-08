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

import ch.admin.bag.covidcertificate.backend.transformation.ws.client.VerificationCheckClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.controller.TransformationController;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.MockHelper;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public abstract class WsBaseConfig {

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

    @Bean
    public TransformationController transformationController(
            MockHelper mockHelper,
            VerificationCheckClient verificationCheckClient,
            OauthWebClient tokenReceiver,
            ZoneId verificationZoneId) {
        return new TransformationController(lightCertificateEnpoint, verificationCheckClient, tokenReceiver, mockHelper, verificationZoneId);
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
    public MockHelper mockHelper() {
        return new MockHelper(mockUrl);
    }

    @Bean
    public VerificationCheckClient verificationCheckClient() {
        return new VerificationCheckClient(verificationCheckBaseUrl, verificationCheckEndpoint);
    }
}
