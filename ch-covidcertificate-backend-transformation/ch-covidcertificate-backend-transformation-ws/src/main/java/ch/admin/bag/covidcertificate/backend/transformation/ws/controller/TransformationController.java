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

import ch.admin.bag.covidcertificate.backend.transformation.model.CertLightPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.HCertPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.PdfPayload;
import ch.admin.bag.covidcertificate.backend.transformation.model.Person;
import ch.admin.bag.covidcertificate.backend.transformation.model.TransformPayload;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.VerificationCheckClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.MockHelper;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;
import ch.ubique.openapi.docannotations.Documentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/app/transform/v1")
public class TransformationController {

    private static final Logger logger = LoggerFactory.getLogger(TransformationController.class);

    private final String lightCertificateEnpoint;
    private final VerificationCheckClient verificationCheckClient;
    private final OauthWebClient oauthWebClient;
    private final ObjectMapper objectMapper;
    private final MockHelper mockHelper;

    public TransformationController(
            String lightCertificateEnpoint,
            VerificationCheckClient verificationCheckClient,
            OauthWebClient tokenReceiver,
            MockHelper mockHelper) {
        this.lightCertificateEnpoint = lightCertificateEnpoint;
        this.verificationCheckClient = verificationCheckClient;
        this.oauthWebClient = tokenReceiver;
        this.mockHelper = mockHelper;

        this.objectMapper = new ObjectMapper();
    }

    @Documentation(
            description = "Echo endpoint",
            responses = {"200 => Hello from CH CovidCertificate Transformation WS"})
    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @GetMapping(path = {"", "/"})
    public @ResponseBody String hello() {
        return "Hello from CH CovidCertificate Transformation WS";
    }

    @Documentation(
            description =
                    "Validates the covid certificate transforms it into a lightcert, and returns it and its qr-code version",
            responses = {
                "200 => Certificate could be validated and transformed",
                "400 => Certificate can't be decoded or is invalid",
                "502 => BIT or Verification gateway failed"
            })
    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @PostMapping(path = "/certificateLight")
    public @ResponseBody ResponseEntity<CertLightPayload> getCertLight(
            @Valid @RequestBody HCertPayload hCertPayload)
            throws IOException, URISyntaxException, InterruptedException {
        // Decode and verify hcert
        final var dccHolder = verificationCheckClient.isValid(hCertPayload);
        if (dccHolder == null) {
            return ResponseEntity.badRequest().build();
        }

        // Create payload for qr light endpoint
        var euCert = dccHolder.getEuDGC();
        var name = euCert.getPerson();

        var person = new Person();
        person.setFn(name.getFamilyName());
        person.setGn(name.getGivenName());
        person.setFnt(name.getStandardizedFamilyName());
        person.setGnt(name.getStandardizedGivenName());

        var transformPayload = new TransformPayload();
        transformPayload.setNam(person);
        transformPayload.setDob(euCert.getDateOfBirth());
        transformPayload.setExp(
                Integer.valueOf((int) (dccHolder.getExpirationTime().toEpochMilli() / 1000)));

        // Get and forward light certificate
        var transformResponse =
                oauthWebClient
                        .getWebClient()
                        .post()
                        .uri(lightCertificateEnpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transformPayload)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        var certLight = objectMapper.readValue(transformResponse, CertLightPayload.class);
        return ResponseEntity.ok(certLight);
    }

    @Documentation(
            description =
                    "Checks that the certificate was issued by the Swiss authorities and generates a new pdf",
            responses = {
                "200 => Certificate could be validated and transformed",
                "400 => Certificate can't be decoded or is invalid",
                "502 => BIT or Verification gateway failed"
            })
    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @PostMapping(path = "/pdf")
    public @ResponseBody ResponseEntity<PdfPayload> getPdf(
            @Valid @RequestBody HCertPayload hCertPayload) throws IOException {
        return ResponseEntity.ok(mockHelper.getCertPdfMock(hCertPayload));
    }
}
