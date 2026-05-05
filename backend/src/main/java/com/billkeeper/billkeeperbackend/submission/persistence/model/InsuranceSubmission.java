package com.billkeeper.billkeeperbackend.submission.persistence.model;

import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "submission")
@Data
public class InsuranceSubmission {

    public enum Status {
        OPEN, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Boolean active;
    private OffsetDateTime dateTime;
    private String name;
    private String eClaimId;

    @Enumerated(EnumType.STRING)
    private InsuranceSubmission.Status status;

    @ManyToOne
    private User user;
}