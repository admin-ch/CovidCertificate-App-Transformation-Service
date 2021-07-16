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
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ResponseParseError;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ValidationException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.OauthWebClient;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;
import ch.ubique.openapi.docannotations.Documentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/app/transform/v1")
public class TransformationController {

    private static final Logger logger = LoggerFactory.getLogger(TransformationController.class);

    private final String lightCertificateEnpoint;
    private final VerificationCheckClient verificationCheckClient;
    private final OauthWebClient oauthWebClient;
    private final ObjectMapper objectMapper;
    private final ZoneId verificationZoneId;
    private final List<String> chIssuers;
    private final boolean debug;

    public TransformationController(
            String lightCertificateEndpoint,
            VerificationCheckClient verificationCheckClient,
            OauthWebClient tokenReceiver,
            ZoneId verificationZoneId,
            List<String> chIssuers,
            boolean debug) {
        this.lightCertificateEnpoint = lightCertificateEndpoint;
        this.verificationCheckClient = verificationCheckClient;
        this.oauthWebClient = tokenReceiver;
        this.verificationZoneId = verificationZoneId;
        this.objectMapper = new ObjectMapper();
        this.chIssuers = chIssuers;
        this.debug = debug;
        logger.info("only accepting the following issuers: {}", chIssuers);
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
            throws IOException, InterruptedException, ValidationException, ResponseParseError {
        // Decode and verify hcert
        final var validationResponse = verificationCheckClient.validate(hCertPayload);
        final var certificateHolder = validationResponse.getHcertDecoded();
        if (certificateHolder == null || !chIssuers.contains(certificateHolder.getIssuer())) {
            return ResponseEntity.badRequest().build();
        }

        // Create payload for qr light endpoint
        var euCert = (DccCert) certificateHolder.getCertificate();
        var name = euCert.getPerson();

        var person = new Person();
        person.setFn(name.getFamilyName());
        person.setGn(name.getGivenName());
        person.setFnt(name.getStandardizedFamilyName());
        person.setGnt(name.getStandardizedGivenName());

        var transformPayload = new TransformPayload();
        transformPayload.setNam(person);
        transformPayload.setDob(euCert.getDateOfBirth());

        var exp =
                validationResponse
                        .getSuccessState()
                        .getValidityRange()
                        .getValidUntil()
                        .atZone(verificationZoneId)
                        .toInstant()
                        .toEpochMilli();
        var nowPlus48 = Instant.now().plus(Duration.ofHours(48)).toEpochMilli();
        transformPayload.setExp((exp < nowPlus48) ? exp : nowPlus48);

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
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> validationFailed(ValidationException e) {
        BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        if (debug) {
            logger.error("Validation failed: {}", e.getState());
            return responseBuilder.body(e.getState());
        } else {
            return responseBuilder.build();
        }
    }

    @ExceptionHandler(ResponseParseError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> validationParseFailed(ResponseParseError e) {
        BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
        if (debug) {
            logger.error("Validation failed (and result could not be parsed): {}", e.getState());
            return responseBuilder.body(e.getState());
        } else {
            return responseBuilder.build();
        }
    }
}
