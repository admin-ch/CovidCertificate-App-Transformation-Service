/*
 * Created by Ubique Innovation AG
 * https://www.ubique.ch
 * Copyright (c) 2021. All rights reserved.
 */

-- rename t_rate_limit
ALTER TABLE t_rate_limit RENAME TO t_transformation_log;
ALTER TABLE t_transformation_log RENAME COLUMN pk_rate_limit TO pk_transformation_log_id;
ALTER TABLE t_transformation_log RENAME CONSTRAINT pk_t_rate_limit TO pk_t_transformation_log_id;

-- add `type` column
ALTER TABLE t_transformation_log ADD COLUMN type character varying(20);
UPDATE t_transformation_log SET type = 'LIGHT_CERT';
ALTER TABLE t_transformation_log ALTER COLUMN type SET NOT NULL;

-- add indexes for `type` and `uvci_hash`
CREATE INDEX idx_transformation_log_type ON t_transformation_log(type);
CREATE INDEX idx_transformation_log_uvci_hash ON t_transformation_log(uvci_hash);