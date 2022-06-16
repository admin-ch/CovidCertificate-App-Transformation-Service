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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformationType;
import ch.admin.bag.covidcertificate.backend.transformation.model.VerificationResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.lightcert.CertLightResponse;
import ch.admin.bag.covidcertificate.backend.transformation.model.pdf.PdfResponse;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckNationalRulesState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckRevocationState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.CheckSignatureState;
import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState.INVALID;
import ch.admin.bag.covidcertificate.sdk.core.verifier.nationalrules.NationalRulesError;
import ch.admin.bag.covidcertificate.sdk.core.verifier.nationalrules.ValidityRange;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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

@TestInstance(Lifecycle.PER_CLASS)
class TransformationControllerTest extends BaseControllerTest {

    private static final Logger logger =
            LoggerFactory.getLogger(TransformationControllerTest.class);

    private static final String BASE_URL = "/app/transform/v1";
    private static final String CERTLIGHT_ENDPOINT = "/certificateLight";
    private static final String PDF_ENDPOINT = "/pdf";

    private static final String LIGHT_CERT_MOCK = "src/main/resources/dev/light-cert-mock.json";
    private CertLightResponse certLightMock;

    private static final String PDF_MOCK = "src/main/resources/dev/cert-pdf-mock.pdf";
    private PdfResponse mockPdfResponse;

    private static final String VERIFICATION_CHECK_SUCCESS_RESPONSE_MOCK =
            "src/main/resources/dev/verification-check-success-response-mock.json";
    private String verificationCheckSuccessResponse;

    private static final int rateLimit = 10;

    @BeforeAll
    public void setup() {
        try {
            certLightMock =
                    objectMapper.readValue(
                            Paths.get(LIGHT_CERT_MOCK).toFile(), CertLightResponse.class);

            var pdfString =
                    Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(PDF_MOCK)));
            mockPdfResponse = new PdfResponse();
            mockPdfResponse.setPdf(pdfString);

            verificationCheckSuccessResponse =
                    Files.readString(Paths.get(VERIFICATION_CHECK_SUCCESS_RESPONSE_MOCK));
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
    public void testActuatorSecurity() throws Exception {
        var response =
                mockMvc.perform(get("/actuator/health"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();
        response =
                mockMvc.perform(get("/actuator/loggers"))
                        .andExpect(status().is(401))
                        .andReturn()
                        .getResponse();
        response =
                mockMvc.perform(
                                get("/actuator/loggers")
                                        .header(
                                                "Authorization",
                                                "Basic cHJvbWV0aGV1czpwcm9tZXRoZXVz"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();
    }

    @Test
    void invalidHcertTest() throws Exception {
        final LocalDateTime now = LocalDateTime.now();

        final String payloadString = getHcertPayloadString();

        final var verificationResponse = new VerificationResponse();
        verificationResponse.setInvalidState(
                new INVALID(
                        CheckSignatureState.SUCCESS.INSTANCE,
                        CheckRevocationState.SUCCESS.INSTANCE,
                        new CheckNationalRulesState.INVALID(
                                NationalRulesError.NO_VALID_PRODUCT, false, "id_0", ""),
                        new ValidityRange(now.minusDays(2), now.plusDays(2)),
                        ""));

        setupVerificationCheckMockServer(
                payloadString, objectMapper.writeValueAsString(verificationResponse));

        mockMvc.perform(
                        post(BASE_URL + CERTLIGHT_ENDPOINT)
                                .content(payloadString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private String getHcertPayloadString() throws JsonProcessingException {
        var hCertPayload = new HCertPayload();
        hCertPayload.setHcert(
                "HC1:NCFB60MG0/3WUWGSLKH47GO0SK7KFDCBOECI9CKW500XK0JCV498F3: BQE64F3+JJ+NMY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7DB8ES8/G8.96Y47ES8.96ZA7$962X6-R8SG6UPC0JCZ69FVCBJ0LVC6JD846KF6C463W5EM6+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946846.96.JCP9EJY8L/5M/5546.96VF63KC/SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6OA73W5Y96B46TPCBEC7ZKW.C2VCDECY CI3DGPC8$CLPCG/DFUCOB8XY8I3D5WEEB8YZAO/EZKEZ967L6256V50MAOCTIEHMJ*E62F8$51G4+KEXLP1ZTO1CS538*5DA0R0QW/3ZHD3IO4OKGBN+WQEBI2+KQ9SM+NRGTV KQ72F%OYWPSOL-0W2.96%25HRG/B16KP:GS%JR$P+24U9MJ4NRE0K89SB9*UB03E.S3P 6QM2/ODSS1WZ73S3LA8W*5.4BI6OLYU53I+*7  HDYKXG6:/7X15F9A.4J2RB0Z1GTLYBS96HSZH%0D5QCU7I+:T8JVUNMZ:7.S6-XOG1LVQD5004KGYHIYMM-$IFKD+KUSEA/YBI04//7:0GYRS7 J51FT.5D2GKYRD.Q$QSJAG.YVQFNLF58GU-M2R6KB5KG/L7JEVY1TRDC-P8YH:R1U425JF0ENJ10PS24EWKPHXAFFZQ");
        final String payloadString = objectMapper.writeValueAsString(hCertPayload);
        return payloadString;
    }

    private void setupVerificationCheckMockServer(String payload, String response)
            throws URISyntaxException {
        final var mockServer = MockRestServiceServer.createServer(rt);
        mockServer
                .expect(
                        ExpectedCount.once(),
                        requestTo(new URI(verificationCheckBaseUrl + verificationCheckEndpoint)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(payload))
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response));
    }

    @Test
    void getCertLightTest() throws Exception {
        boolean rateLimitTested = false;
        final String hcertPayloadString = getHcertPayloadString();
        for (int i = 0; i < rateLimit + 1; i++) {
            setupVerificationCheckMockServer(hcertPayloadString, verificationCheckSuccessResponse);
            boolean rateLimitExceeded = i >= rateLimit;
            HttpStatus expectedStatus =
                    rateLimitExceeded ? HttpStatus.TOO_MANY_REQUESTS : HttpStatus.OK;
            final MockHttpServletResponse response =
                    requestTransformation(
                            hcertPayloadString, TransformationType.LIGHT_CERT, expectedStatus);
            if (!rateLimitExceeded) {
                final var responsePayload =
                        objectMapper.readValue(
                                response.getContentAsString(), CertLightResponse.class);
                assertEquals(certLightMock.getQrCode(), responsePayload.getQrCode());
            } else {
                rateLimitTested = true;
            }
        }
        assertTrue(rateLimitTested);
    }

    private MockHttpServletResponse requestTransformation(
            String hcertPayloadString, TransformationType type, HttpStatus expectedStatus)
            throws Exception {
        String url = BASE_URL;
        switch (type) {
            case LIGHT_CERT:
                url += CERTLIGHT_ENDPOINT;
                break;
            case PDF:
                url += PDF_ENDPOINT;
                break;
            default:
                throw new RuntimeException("received unexpected type: " + type);
        }
        return mockMvc.perform(
                        post(url)
                                .content(hcertPayloadString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus.value()))
                .andReturn()
                .getResponse();
    }

    @Test
    void getPdfTest() throws Exception {
        boolean rateLimitTested = false;
        final String hcertPayloadString = getHcertPayloadString();
        for (int i = 0; i < rateLimit + 1; i++) {
            setupVerificationCheckMockServer(hcertPayloadString, verificationCheckSuccessResponse);
            boolean rateLimitExceeded = i >= rateLimit;
            HttpStatus expectedStatus =
                    rateLimitExceeded ? HttpStatus.TOO_MANY_REQUESTS : HttpStatus.OK;
            final MockHttpServletResponse response =
                    requestTransformation(
                            hcertPayloadString, TransformationType.PDF, expectedStatus);
            if (!rateLimitExceeded) {
                final var responsePayload =
                        objectMapper.readValue(response.getContentAsString(), PdfResponse.class);
                assertEquals(mockPdfResponse.getPdf(), responsePayload.getPdf());
            } else {
                rateLimitTested = true;
            }
        }
        assertTrue(rateLimitTested);
    }
}
