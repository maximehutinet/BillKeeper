package com.billkeeper.billkeeperbackend.bill.api.model;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateBillStatusRequest {
    private Bill.Status status;
}
