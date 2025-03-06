CREATE TABLE billkeeperuser
(
    id                   UUID        NOT NULL
        CONSTRAINT billkeeperuser_pkey
            PRIMARY KEY,
    keycloak_id          TEXT UNIQUE NOT NULL,
    firstname            TEXT UNIQUE NOT NULL,
    email                TEXT UNIQUE NOT NULL,
    profile_picture_name TEXT
);

CREATE TABLE beneficiary
(
    id        UUID    NOT NULL
        CONSTRAINT beneficiary_pkey
            PRIMARY KEY,
    active    BOOLEAN NOT NULL,
    firstname TEXT
);

CREATE TABLE submission
(
    id         UUID      NOT NULL
        CONSTRAINT submission_pkey
            PRIMARY KEY,
    active     BOOLEAN   NOT NULL,
    date_time  TIMESTAMP NOT NULL,
    name       TEXT,
    e_claim_id TEXT
);

CREATE TABLE bill
(
    id                UUID      NOT NULL
        CONSTRAINT bill_pkey
            PRIMARY KEY,
    active            BOOLEAN   NOT NULL,
    date_time         TIMESTAMP NOT NULL,
    name              TEXT,
    amount            DECIMAL,
    user_id           UUID
        CONSTRAINT fk_user_id REFERENCES billkeeperuser,
    currency          VARCHAR(255) CHECK (currency IN ('CHF', 'EUR')),
    service_date_time TIMESTAMP,
    paid_date_time    TIMESTAMP,
    provider          TEXT,
    status            VARCHAR(255) CHECK (status IN ('TO_FILE', 'FILED', 'REIMBURSED')),
    beneficiary_id    UUID
        CONSTRAINT fk_beneficiary_id REFERENCES beneficiary,
    submission_id     UUID
        CONSTRAINT fk_submission_id REFERENCES submission
);

CREATE TABLE comment
(
    id        UUID      NOT NULL
        CONSTRAINT comment_pkey
            PRIMARY KEY,
    active    BOOLEAN   NOT NULL,
    date_time TIMESTAMP NOT NULL,
    content   TEXT,
    user_id   UUID
        CONSTRAINT fk_user_id REFERENCES billkeeperuser,
    bill_id   UUID
        CONSTRAINT fk_bill_id REFERENCES bill
);

CREATE TABLE document
(
    id          UUID      NOT NULL
        CONSTRAINT document_pkey
            PRIMARY KEY,
    active      BOOLEAN   NOT NULL,
    date_time   TIMESTAMP NOT NULL,
    name        TEXT,
    description TEXT,
    bill_id     UUID
        CONSTRAINT fk_bill_id REFERENCES bill
);

CREATE TABLE settings
(
    id                        UUID      NOT NULL
        CONSTRAINT settings_pkey
            PRIMARY KEY,
    active                    BOOLEAN   NOT NULL,
    date_time                 TIMESTAMP NOT NULL,
    euro_to_usd_exchange_rate DECIMAL,
    chf_to_usd_exchange_rate  DECIMAL
);