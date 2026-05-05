ALTER TABLE bill
    DROP CONSTRAINT bill_status_check;

ALTER TABLE bill
    ADD CONSTRAINT bill_status_check
        CHECK (status IN ('TO_FILE', 'FILED', 'REIMBURSEMENT_IN_PROGRESS', 'REIMBURSED', 'REJECTED'));

ALTER TABLE submission
    ADD status VARCHAR(255);

UPDATE submission
SET status = 'OPEN'
WHERE submission.e_claim_id IS NOT NULL;

ALTER TABLE submission
    ADD CONSTRAINT submission_status_check
        CHECK (status IN ('OPEN', 'CLOSED'));

CREATE TABLE family
(
    id       UUID NOT NULL
        CONSTRAINT family_pkey
            PRIMARY KEY,
    name     TEXT,
    owner_id UUID NOT NULL
        CONSTRAINT fk_user_id REFERENCES billkeeperuser
);

ALTER TABLE billkeeperuser
    ADD family_id UUID
        CONSTRAINT fk_family_id REFERENCES family;

CREATE TABLE invitation
(
    id              UUID      NOT NULL
        CONSTRAINT invitation_pkey
            PRIMARY KEY,
    active          BOOLEAN   NOT NULL,
    date_time       TIMESTAMP NOT NULL,
    author_id       UUID      NOT NULL
        CONSTRAINT fk_user_id REFERENCES billkeeperuser,
    recipient_email TEXT      NOT NULL,
    type            VARCHAR(255) CHECK (type IN ('JOIN_FAMILY')),
    status          VARCHAR(255) CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);

ALTER TABLE document
    ADD user_id UUID
        CONSTRAINT fk_user_id REFERENCES billkeeperuser;

UPDATE document
SET user_id = b.user_id
FROM bill b WHERE document.bill_id = b.id;

ALTER TABLE submission
    ADD user_id UUID
        CONSTRAINT fk_user_id REFERENCES billkeeperuser;

UPDATE submission s
SET user_id = b.user_id
FROM bill b WHERE b.submission_id = s.id;

ALTER TABLE submission
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE billkeeperuser DROP CONSTRAINT billkeeperuser_firstname_key;

ALTER TABLE billkeeperuser
    ALTER COLUMN firstname SET NOT NULL;
