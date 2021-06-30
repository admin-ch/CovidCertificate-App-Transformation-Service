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

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

class TransformationControllerTest extends BaseControllerTest {

    private final String BASE_URL = "/v1/transform";
    private final String CERTLIGHT_ENDPOINT = "/certificateLight";
    private final String PDF_ENDPOINT = "/pdf";

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
