package com.billkeeper.billkeeperbackend.submission.api.model;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class InsuranceSubmissionResponse {
    private UUID id;
    private Boolean active;
    private OffsetDateTime dateTime;
    private String name;
    private String eClaimId;
    private List<Bill> bills;
    private Double totalUsdAmount;
}