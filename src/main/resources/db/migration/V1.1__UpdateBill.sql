ALTER TABLE bill
DROP CONSTRAINT bill_status_check;

ALTER TABLE bill
ADD CONSTRAINT bill_status_check
CHECK (status IN ('TO_FILE', 'FILED', 'REIMBURSED', 'REJECTED'));