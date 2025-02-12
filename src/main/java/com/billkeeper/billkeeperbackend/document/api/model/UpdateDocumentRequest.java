package com.billkeeper.billkeeperbackend.document.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateDocumentRequest {
    private String description;
}