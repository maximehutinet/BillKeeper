ALTER TABLE bill
    ADD COLUMN reimbursement_date_time TIMESTAMP,
    ADD COLUMN reimbursed_amount DECIMAL;

CREATE INDEX idx_bill_user_active_datetime
    ON bill(user_id, active, date_time DESC);

CREATE INDEX idx_bill_status_active
    ON bill(status, active);

CREATE INDEX idx_bill_paid_datetime
    ON bill(paid_date_time) WHERE paid_date_time IS NULL;

CREATE INDEX idx_bill_beneficiary
    ON bill(beneficiary_id);

CREATE INDEX idx_bill_submission
    ON bill(submission_id);

CREATE INDEX idx_parsingjob_bill
    ON parsingjob(bill_id);

CREATE INDEX idx_comment_bill
    ON comment(bill_id);

CREATE INDEX idx_document_bill
    ON document(bill_id);

CREATE INDEX idx_document_user
    ON document(user_id);

CREATE INDEX idx_submission_user
    ON submission(user_id);

CREATE INDEX idx_user_family
    ON billkeeperuser(family_id);

CREATE INDEX idx_bill_provider
    ON bill(lower(provider));

CREATE INDEX idx_user_firstname
    ON billkeeperuser(lower(firstname));