package com.billkeeper.billkeeperbackend.bill.api.model;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
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
    private Beneficiary beneficiary;
    private InsuranceSubmission submission;
    private ParsingJob.Status parsingJobStatus;

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
        this.beneficiary = bill.getBeneficiary();
        this.submission = bill.getSubmission();
        this.parsingJobStatus = parsingJobStatus;
    }

    public BillResponse (Bill bill) {
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
        this.beneficiary = bill.getBeneficiary();
        this.submission = bill.getSubmission();
    }
}