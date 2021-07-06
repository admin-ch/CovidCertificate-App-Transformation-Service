/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.admin.bag.covidcertificate.backend.transformation.ws.util;

import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

public class OauthWebClient {
    private final WebClient client;

    public OauthWebClient(String clientId, ClientRegistrationRepository clientRegistrations) {
        var clientRepository = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrations, new InMemoryOAuth2AuthorizedClientService(clientRegistrations));
        var oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRepository);
        oauth.setDefaultClientRegistrationId(clientId);
        client = WebClient.builder().filter(oauth).build();
    }

    public WebClient getWebClient() {
        return client;
    }

}