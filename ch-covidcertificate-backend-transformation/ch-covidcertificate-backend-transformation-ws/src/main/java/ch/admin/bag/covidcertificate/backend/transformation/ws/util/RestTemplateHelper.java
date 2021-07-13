/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.transformation.ws.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateHelper {

    private static final String COVIDCERT_TRANSFORMATION = "covidcert-transformation";
    private static final int CONNECT_TIMEOUT = 20000;
    private static final int SOCKET_TIMEOUT = 20000;

    private RestTemplateHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static RestTemplate getRestTemplate() {
        return buildRestTemplate();
    }

    private static RestTemplate buildRestTemplate() {
        RestTemplate rt = null;
        rt = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        rt.setInterceptors(interceptors);
        return rt;
    }

    private static CloseableHttpClient httpClient() {
        var manager = new PoolingHttpClientConnectionManager();

        HttpClientBuilder builder = HttpClients.custom();
        builder.useSystemProperties().setUserAgent(COVIDCERT_TRANSFORMATION);

        manager.setDefaultMaxPerRoute(20);
        manager.setMaxTotal(30);

        builder.setConnectionManager(manager)
                .disableCookieManagement()
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(CONNECT_TIMEOUT)
                                .setSocketTimeout(SOCKET_TIMEOUT)
                                .build());
        return builder.build();
    }
}
