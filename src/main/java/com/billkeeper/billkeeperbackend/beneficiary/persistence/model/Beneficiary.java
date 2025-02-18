package com.billkeeper.billkeeperbackend.beneficiary.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "beneficiary")
@Data
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Boolean active;
    private OffsetDateTime dateTime;
    private String firstname;
}