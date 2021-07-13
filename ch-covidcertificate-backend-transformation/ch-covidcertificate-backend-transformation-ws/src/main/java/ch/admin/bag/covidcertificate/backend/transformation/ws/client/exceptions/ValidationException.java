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

import ch.admin.bag.covidcertificate.sdk.core.models.state.VerificationState;

public class ValidationException extends Exception {
    private final VerificationState state;

    public ValidationException(VerificationState state) {
        this.state = state;
    }

    public VerificationState getState() {
        return state;
    }
}
