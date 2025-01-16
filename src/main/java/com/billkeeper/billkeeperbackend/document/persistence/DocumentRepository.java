package com.billkeeper.billkeeperbackend.document.persistence;

import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DocumentRepository extends CrudRepository<Document, UUID> {
}