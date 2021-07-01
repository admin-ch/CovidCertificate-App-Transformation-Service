/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.transformation.ws.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

class TransformationControllerTest extends BaseControllerTest {

    private static final Logger logger =
            LoggerFactory.getLogger(TransformationControllerTest.class);

    private static final String BASE_URL = "/app/transform/v1";
    private static final String CERTLIGHT_ENDPOINT = "/certificateLight";
    private static final String PDF_ENDPOINT = "/pdf";
    private static final String LIGHT_CERT_MOCK = "src/main/resources/light-cert-mock.json";

    private static CertLightPayload certLightMock;

    static {
        try {
            certLightMock =
                    new ObjectMapper()
                            .readValue(Paths.get(LIGHT_CERT_MOCK).toFile(), CertLightPayload.class);
        } catch (IOException e) {
            logger.error("Couldn't parse light cert mock file");
        }
    }

    @Test
    void helloTest() throws Exception {
        final MockHttpServletResponse response =
                mockMvc.perform(get(BASE_URL + "/").accept(MediaType.TEXT_PLAIN))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();

        assertNotNull(response);
        assertEquals(
                "Hello from CH CovidCertificate Transformation WS", response.getContentAsString());
    }

    @Test
    @Disabled("Need to mock AWS endpoint")
    void getCertLightTest() throws Exception {
        var hCertPayload = new HCertPayload();
        hCertPayload.setHcert("HC1:example");
        final MockHttpServletResponse response =
                mockMvc.perform(
                                post(BASE_URL + CERTLIGHT_ENDPOINT)
                                        .content(objectMapper.writeValueAsString(hCertPayload))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();
        final var responsePayload =
                objectMapper.readValue(response.getContentAsString(), CertLightPayload.class);
        assertEquals(certLightMock.getQrcode(), responsePayload.getQrcode());
    }

    @Test
    void getPdfTest() throws Exception {
        var hCertPayload = new HCertPayload();
        hCertPayload.setHcert("HC1:example");
        final MockHttpServletResponse response =
                mockMvc.perform(
                                post(BASE_URL + PDF_ENDPOINT)
                                        .content(objectMapper.writeValueAsString(hCertPayload))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();
    }
}
