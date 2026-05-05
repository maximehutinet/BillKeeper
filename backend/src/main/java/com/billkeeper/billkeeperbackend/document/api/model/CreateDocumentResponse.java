package com.billkeeper.billkeeperbackend.document.api.model;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import org.springframework.stereotype.Service;

@Service
public class CreateDocumentResponse {

    private final AppConfig appConfig;

    public CreateDocumentResponse(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public DocumentResponse create(Document document) {
        return DocumentResponse
                .builder()
                .id(document.getId())
                .dateTime(document.getDateTime())
                .url(appConfig.getServerUrl() + "/documents/" + document.getId())
                .description(document.getDescription())
                .build();
    }
}