ALTER TABLE bill
DROP CONSTRAINT bill_status_check;

ALTER TABLE bill
ADD CONSTRAINT bill_status_check
CHECK (status IN ('TO_FILE', 'FILED', 'REIMBURSED', 'REJECTED'));

CREATE TABLE parsingjob
(
    id                UUID      NOT NULL
        CONSTRAINT parsingjob_pkey
            PRIMARY KEY,
    date_time         TIMESTAMP NOT NULL,
    status            VARCHAR(255) CHECK (status IN ('IN_PROGRESS', 'SUCCESS', 'FAILED')),
    bill_id   UUID
        CONSTRAINT fk_bill_id REFERENCES bill
);