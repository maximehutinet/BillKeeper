package com.billkeeper.billkeeperbackend.beneficiary.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BeneficiaryResponse {
    private UUID id;
    private Boolean active;
    private String firstname;
}
