package com.billkeeper.billkeeperbackend.bill.api.model;

import com.billkeeper.billkeeperbackend.beneficiary.api.model.BeneficiaryResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class BillResponse {
    private UUID id;
    private OffsetDateTime dateTime;
    private String name;
    private Double amount;
    private UserResponse user;
    private Bill.Currency currency;
    private OffsetDateTime serviceDateTime;
    private OffsetDateTime paidDateTime;
    private String provider;
    private Bill.Status status;
    private BeneficiaryResponse beneficiary;
    private UUID submissionId;
    private ParsingJob.Status parsingJobStatus;
    private OffsetDateTime reimbursementDateTime;
    private Double reimbursedAmount;

    public BillResponse(Bill bill, ParsingJob.Status parsingJobStatus) {
        this.id = bill.getId();
        this.dateTime = bill.getDateTime();
        this.name = bill.getName();
        this.amount = bill.getAmount();
        this.user = new UserResponse(bill.getUser());
        this.currency = bill.getCurrency();
        this.serviceDateTime = bill.getServiceDateTime();
        this.paidDateTime = bill.getPaidDateTime();
        this.provider = bill.getProvider();
        this.status = bill.getStatus();
        this.beneficiary = bill.getBeneficiary() != null ? new BeneficiaryResponse(bill.getBeneficiary()) : null;
        this.submissionId = bill.getSubmission() != null ? bill.getSubmission().getId() : null;
        this.parsingJobStatus = parsingJobStatus;
        this.reimbursementDateTime = bill.getReimbursementDateTime();
        this.reimbursedAmount = bill.getReimbursedAmount();
    }

    public BillResponse (Bill bill) {
        this(bill, null);
    }
}