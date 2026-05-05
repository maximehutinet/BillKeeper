package com.billkeeper.billkeeperbackend.beneficiary.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUpdateBeneficiaryRequest {
    @NotBlank(message = "Firstname cannot be empty")
    String firstname;
}
