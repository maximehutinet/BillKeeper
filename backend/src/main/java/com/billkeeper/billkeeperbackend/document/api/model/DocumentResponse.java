package com.billkeeper.billkeeperbackend.document.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class DocumentResponse {
    private UUID id;
    private OffsetDateTime dateTime;
    private String url;
    private String description;
}