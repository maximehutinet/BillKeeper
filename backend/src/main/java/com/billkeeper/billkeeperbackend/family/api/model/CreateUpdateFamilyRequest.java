package com.billkeeper.billkeeperbackend.family.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateUpdateFamilyRequest {
    @NotBlank
    private String name;
    private List<UUID> membersId;
}
