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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;


public class TokenReceiver {
    private final String grantType;
    private final String scope;
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private Instant expiresAt = null;
    private String bearer = null;

    public TokenReceiver(String tokenEndpoint, String grantType, String scope, String clientId, String clientSecret) {
        this.tokenEndpoint = tokenEndpoint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.scope = scope;

        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String getBearer() throws URISyntaxException, IOException, InterruptedException {
        return getBearer(false);
    }

    public String getBearer(Boolean force) throws URISyntaxException, IOException, InterruptedException {
        if(expiresAt != null && expiresAt.isAfter(Instant.now()) && !force) {
            return bearer;
        }
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(tokenEndpoint))
            .header("Content-Type" , "application/x-www-form-urlencoded")
            .POST(
            BodyPublishers.ofString(
                "grant_type=" + grantType
                + "&client_secret=" + clientSecret
                + "&client_id=" + clientId
                + "&scope=" + scope))
                .build();
        final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        final var token = objectMapper.readValue(response.body(), TokenResponse.class);
        if(token == null) {
            return "";
        }
        expiresAt = Instant.now().plusSeconds(token.getExpiresIn() - 50);
        bearer = "Bearer " + token.getAccessToken();
        return bearer;
    }

   
}