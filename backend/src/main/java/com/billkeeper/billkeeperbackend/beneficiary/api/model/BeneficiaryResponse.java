package com.billkeeper.billkeeperbackend.beneficiary.api.model;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
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

    public BeneficiaryResponse(Beneficiary beneficiary) {
        this.id = beneficiary.getId();
        this.active = beneficiary.getActive();
        this.firstname = beneficiary.getFirstname();
    }
}
