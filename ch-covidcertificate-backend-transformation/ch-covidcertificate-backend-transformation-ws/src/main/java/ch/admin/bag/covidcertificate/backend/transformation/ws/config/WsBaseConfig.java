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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class WsBaseConfig {

    @Value("${mock.url:test}")
    private String mockUrl;

    @Bean
    public TransformationController transformationController(MockHelper mockHelper) {
        return new TransformationController(mockHelper);
    }

    @Bean
    public MockHelper mockHelper() {
        return new MockHelper(mockUrl);
    }
}
