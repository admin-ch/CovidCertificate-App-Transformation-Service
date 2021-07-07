// Copyright (c) 2021 Patrick Amrein <amrein@ubique.ch>
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions;

import com.fasterxml.jackson.databind.JsonNode;

public class ResponseParseError extends Exception {
    private final JsonNode state;
    public ResponseParseError(JsonNode state) {
        this.state = state;
    }

    public String getState() {
        return state.toPrettyString();
    }
}
