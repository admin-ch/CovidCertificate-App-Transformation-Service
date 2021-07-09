/*
 * Created by Ubique Innovation AG
 * https://www.ubique.ch
 * Copyright (c) 2021. All rights reserved.
 */

CREATE TABLE t_rate_limit
(
    pk_rate_limit integer generated always as IDENTITY,
    uvci_hash     Character varying(64)    NOT NULL,
    created_at    timestamp with time zone NOT NULL DEFAULT now(),
    CONSTRAINT pk_t_rate_limit PRIMARY KEY (pk_rate_limit)
);

CREATE TABLE t_shedlock
(
    name       VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at  TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),
    CONSTRAINT pk_t_shedlock PRIMARY KEY (name)
);