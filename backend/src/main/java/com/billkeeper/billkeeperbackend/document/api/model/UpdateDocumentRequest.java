package com.billkeeper.billkeeperbackend.document.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateDocumentRequest {
    private String description;
    private UUID billId;
}