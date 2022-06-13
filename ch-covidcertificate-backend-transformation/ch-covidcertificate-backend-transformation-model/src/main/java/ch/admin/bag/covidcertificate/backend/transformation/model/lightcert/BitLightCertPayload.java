/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.admin.bag.covidcertificate.backend.transformation.model.lightcert;

import ch.admin.bag.covidcertificate.backend.transformation.model.Person;

public class BitLightCertPayload {
    private Person nam;
    private String dob;
    private Long exp;

    public Person getNam() {
        return this.nam;
    }

    public void setNam(Person nam) {
        this.nam = nam;
    }

    public String getDob() {
        return this.dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Long getExp() {
        return this.exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }
}
