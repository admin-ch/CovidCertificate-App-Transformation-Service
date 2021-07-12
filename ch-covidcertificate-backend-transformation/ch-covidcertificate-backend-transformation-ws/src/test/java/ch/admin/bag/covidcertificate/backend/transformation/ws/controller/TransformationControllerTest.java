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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.PdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.VerificationResponse;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckNationalRulesState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckRevocationState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckSignatureState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState.INVALID;
import ch.admin.bag.covidcertificate.sdk.core.verifier.nationalrules.NationalRulesError;
import ch.admin.bag.covidcertificate.sdk.core.verifier.nationalrules.ValidityRange;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class TransformationControllerTest extends BaseControllerTest {

    private static final Logger logger =
            LoggerFactory.getLogger(TransformationControllerTest.class);

    private static final String BASE_URL = "/app/transform/v1";
    private static final String CERTLIGHT_ENDPOINT = "/certificateLight";
    private static final String PDF_ENDPOINT = "/pdf";
    private static final String LIGHT_CERT_MOCK = "src/main/resources/light-cert-mock.json";
    private static CertLightPayload certLightMock;
    private static PdfPayload mockPdfPayload;

    static {
        try {
            certLightMock =
                    new ObjectMapper()
                            .readValue(Paths.get(LIGHT_CERT_MOCK).toFile(), CertLightPayload.class);
            var pdfString =
                    Base64.getEncoder()
                            .encodeToString(
                                    Files.readAllBytes(
                                            Paths.get("src/main/resources/cert-pdf-mock.pdf")));
            mockPdfPayload = new PdfPayload();
            mockPdfPayload.setPdf(pdfString);
        } catch (IOException e) {
            logger.error("Couldn't parse light cert mock file");
        }
    }

    @Autowired RestTemplate rt;

    @Value("${verification.check.baseurl}")
    private String verificationCheckBaseUrl;

    @Value("${verification.check.endpoint}")
    private String verificationCheckEndpoint;

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
    void invalidHcertTest() throws Exception {
        final LocalDateTime now = LocalDateTime.now();

        var hCertPayload = new HCertPayload();
        hCertPayload.setHcert("HC1:example");
        final String hcertPayload = objectMapper.writeValueAsString(hCertPayload);

        final var verificationResponse = new VerificationResponse();
        verificationResponse.setInvalidState(
                new INVALID(
                        CheckSignatureState.SUCCESS.INSTANCE,
                        CheckRevocationState.SUCCESS.INSTANCE,
                        new CheckNationalRulesState.INVALID(
                                NationalRulesError.NO_VALID_PRODUCT, "id_0"),
                        new ValidityRange(now.minusDays(2), now.plusDays(2))));

        final var mockServer = MockRestServiceServer.createServer(rt);
        mockServer
                .expect(
                        ExpectedCount.once(),
                        requestTo(new URI(verificationCheckBaseUrl + verificationCheckEndpoint)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(hcertPayload))
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(verificationResponse)));

        mockMvc.perform(
                        post(BASE_URL + CERTLIGHT_ENDPOINT)
                                .content(objectMapper.writeValueAsString(hCertPayload))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Disabled("Need to mock QR-Light endpoint.")
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
        assertEquals(certLightMock.getQrCode(), responsePayload.getQrCode());
    }

    @Test
    @Disabled("PDF endpoint isn't implemented, yet.")
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
        final var responsePayload =
                objectMapper.readValue(response.getContentAsString(), PdfPayload.class);
        assertEquals(mockPdfPayload.getPdf(), responsePayload.getPdf());
    }
}
