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
import ch.ubique.openapi.docannotations.Documentation;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1/transform")
public class TransformationController {

    private static final Logger logger = LoggerFactory.getLogger(TransformationController.class);

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
            responses = {"200 => Certificate could be validated and transformed"})
    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @PostMapping(path = "/certificateLight")
    public @ResponseBody ResponseEntity<CertLightPayload> getCertLight(
            @Valid @RequestBody HCertPayload hCertPayload) {
        return ResponseEntity.ok().build();
    }

    @Documentation(
            description =
                    "Checks that the certificate was issued by the Swiss authorities and generates a new pdf",
            responses = {"200 => Certificate could be validated and transformed"})
    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @PostMapping(path = "/pdf")
    public @ResponseBody ResponseEntity<PdfPayload> getPdf(
            @Valid @RequestBody HCertPayload hCertPayload) {
        return ResponseEntity.ok().build();
    }
}
