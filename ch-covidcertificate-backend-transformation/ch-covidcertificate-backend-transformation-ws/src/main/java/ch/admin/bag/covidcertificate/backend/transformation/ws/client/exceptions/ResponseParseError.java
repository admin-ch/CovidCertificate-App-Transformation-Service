/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

public class ResponseParseError extends Exception {
    private final JsonNode state;

    public ResponseParseError(JsonNode state) {
        this.state = state;
    }

    public String getState() {
        if (state == null) {
            return "{\"errorCode\": \"S|UNK\"}";
        }
        if (!(state.get("invalidState") instanceof NullNode)) {
            return state.get("invalidState").toPrettyString();
        }
        if (!(state.get("errorState") instanceof NullNode)) {
            return state.get("errorState").toPrettyString();
        }
        return "{\"errorCode\": \"S|UNK\"}";
    }
}
