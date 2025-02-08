package com.billkeeper.billkeeperbackend.submission.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateUpdateInsuranceSubmissionRequest {
    String name;
    List<UUID> billIds;
}