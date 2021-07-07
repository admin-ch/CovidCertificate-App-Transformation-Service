// Copyright (c) 2021 Patrick Amrein <amrein@ubique.ch>
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package ch.admin.bag.covidcertificate.backend.transformation.ws.client.exceptions;

import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState;

public class ValidationException extends Exception {
    private final VerificationState state;
    public ValidationException(VerificationState state){
        this.state = state;
    }
    public VerificationState getState() {
        return state;
    }
}
