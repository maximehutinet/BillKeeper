package com.billkeeper.billkeeperbackend.family.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddMemberToFamilyRequest {
    @NotBlank
    @Email
    private String email;
}