package com.billkeeper.billkeeperbackend.bill.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UpdateBillReimbursementRequest {
    private OffsetDateTime reimbursementDateTime;
    private Double reimbursedAmount;
}
