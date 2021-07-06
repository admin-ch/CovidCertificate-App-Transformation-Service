/*
 * Copyright (c) 2021 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.admin.bag.covidcertificate.backend.transformation.model;

public class Person {
    private String fn;
    private String gn;
    private String fnt;
    private String gnt;


    public String getFn() {
        return this.fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getGn() {
        return this.gn;
    }

    public void setGn(String gn) {
        this.gn = gn;
    }

    public String getFnt() {
        return this.fnt;
    }

    public void setFnt(String fnt) {
        this.fnt = fnt;
    }

    public String getGnt() {
        return this.gnt;
    }

    public void setGnt(String gnt) {
        this.gnt = gnt;
    }

}
