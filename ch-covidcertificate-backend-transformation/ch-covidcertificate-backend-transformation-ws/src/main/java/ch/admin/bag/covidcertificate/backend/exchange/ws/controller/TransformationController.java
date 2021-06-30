/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.exchange.ws.controller;

import ch.ubique.openapi.docannotations.Documentation;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/transformation/v1/")
public class TransformationController {

    private static final Logger logger = LoggerFactory.getLogger(TransformationController.class);

    @PostConstruct
    public void init() {
        logger.info("Started controller!");
    }

    @Documentation(
            description = "Echo endpoint",
            responses = {"200 => Hello from CH CovidCertificate Transformation WS"})
    @GetMapping("")
    public @ResponseBody String hello() {
        return "Hello from CH CovidCertificate Transformation WS";
    }
}
