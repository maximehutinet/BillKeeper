ALTER TABLE bill
    DROP CONSTRAINT bill_status_check;

ALTER TABLE bill
    ADD CONSTRAINT bill_status_check
        CHECK (status IN ('TO_FILE', 'FILED', 'REIMBURSEMENT_IN_PROGRESS', 'REIMBURSED', 'REJECTED'));

ALTER TABLE submission
    ADD status VARCHAR(255);

UPDATE submission
SET status = 'OPEN'
WHERE submission.e_claim_id IS NULL;

ALTER TABLE submission
    ADD CONSTRAINT submission_status_check
        CHECK (status IN ('OPEN', 'CLOSED'));