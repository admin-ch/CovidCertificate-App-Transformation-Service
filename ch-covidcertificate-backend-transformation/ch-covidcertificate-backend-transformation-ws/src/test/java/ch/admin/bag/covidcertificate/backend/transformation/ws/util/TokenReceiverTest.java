/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.transformation.ws.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TokenReceiverTest {

    // @Test
    public void testToken() throws Exception {
        var tokenReceiver = new TokenReceiver(
            "<token_endpoint>", 
            "client_credentials",
            "openid", 
            "<client_id>",
             "<client_secret>");
        var token = tokenReceiver.getBearer();
        var secondToken = tokenReceiver.getBearer();
        assertEquals(token, secondToken);
        var thirdToken = tokenReceiver.getBearer(true);
        assertNotEquals(token, thirdToken);
    }
}
