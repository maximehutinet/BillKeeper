package com.billkeeper.billkeeperbackend.bill.api.model;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UpdateBillRequest {
    private String name;
    private OffsetDateTime serviceDateTime;
    private OffsetDateTime paidDateTime;
    private OffsetDateTime reimbursementDateTime;
    private Double reimbursedAmount;
    private Double amount;
    private Bill.Currency currency;
    private String provider;
    private Bill.Status status;
    private Beneficiary beneficiary;
}
