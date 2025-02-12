package com.billkeeper.billkeeperbackend.document.persistence.model;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "document")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private OffsetDateTime dateTime;
    private Boolean active;
    private String name;
    private String description;

    @ManyToOne
    private Bill bill;
}