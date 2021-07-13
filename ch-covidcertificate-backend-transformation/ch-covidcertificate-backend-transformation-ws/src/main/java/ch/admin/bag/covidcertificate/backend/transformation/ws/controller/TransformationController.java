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
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.CertLightClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.VerificationCheckClient;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.CertificateFormatException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.EmptyCertificateException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.MultipleEntriesException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.RateLimitExceededException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ResponseParseError;
import ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions.ValidationException;
import ch.admin.bag.covidcertificate.backend.transformation.ws.service.RateLimitService;
import ch.admin.bag.covidcertificate.backend.transformation.ws.util.DccHelper;
import ch.admin.bag.covidcertificate.sdk.core.models.healthcert.eu.DccCert;
import ch.ubique.openapi.docannotations.Documentation;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    private final VerificationCheckClient verificationCheckClient;
    private final CertLightClient certLightClient;
    private final RateLimitService rateLimitService;
    private final boolean debug;

    public TransformationController(
            VerificationCheckClient verificationCheckClient,
            CertLightClient certLightClient,
            RateLimitService rateLimitService,
            boolean debug) {
        this.verificationCheckClient = verificationCheckClient;
        this.certLightClient = certLightClient;
        this.rateLimitService = rateLimitService;
        this.debug = debug;
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
                    "Validates the covid certificate, transforms it into a light certificate, and returns it and its qr-code version",
            responses = {
                "200 => Certificate could be validated and transformed",
                "400 => Certificate can't be decoded or is invalid",
                "502 => BIT or Verification gateway failed"
            })
    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @PostMapping(path = "/certificateLight")
    public @ResponseBody ResponseEntity<CertLightPayload> getCertLight(
            @Valid @RequestBody HCertPayload hCertPayload)
            throws IOException, ValidationException, ResponseParseError, NoSuchAlgorithmException,
                    RateLimitExceededException, EmptyCertificateException,
                    MultipleEntriesException {

        // Decode and verify hcert
        final var validationResponse = verificationCheckClient.validate(hCertPayload);
        final var certificateHolder = validationResponse.getHcertDecoded();
        if (certificateHolder == null) {
            return ResponseEntity.badRequest().build();
        }

        var euCert = (DccCert) certificateHolder.getCertificate();

        final String uvci = DccHelper.getUvci(euCert);
        rateLimitService.checkRateLimit(uvci);

        final var validityRange = validationResponse.getSuccessState().getValidityRange();
        CertLightPayload certLight = certLightClient.getCertLight(euCert, validityRange);
        rateLimitService.updateCount(uvci);

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
            @Valid @RequestBody HCertPayload hCertPayload) {
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

    @ExceptionHandler({EmptyCertificateException.class, MultipleEntriesException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Void> euDgcInvalidType(CertificateFormatException e) {
        if (e instanceof EmptyCertificateException) {
            logger.error("HCert was decoded but didn't contain any entries!");
        } else {
            logger.error("HCert was decoded but contained multiple entries!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<Void> rateLimitExceeded(RateLimitExceededException e) {
        logger.info("Rate limit exceeded for uvci-hash: {}", e.getUvciHash());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
