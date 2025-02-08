package com.billkeeper.billkeeperbackend.submission.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "submission")
@Data
public class InsuranceSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Boolean active;
    private OffsetDateTime dateTime;
    private String name;
}