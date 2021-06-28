/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.exchange.ws.config;

@Configuration
@ComponentScan(basePackages = {"ch.admin.bag.covidcertificate.backend.exchange.ws.config"})
@EnableAutoConfiguration
@EnableWebMvc
public class ExchangeWS {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
