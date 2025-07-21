package com.billkeeper.billkeeperbackend.bill.persistence.model;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bill")
@Data
public class Bill {

    public enum Status {
        TO_PAY, TO_FILE, FILED, REIMBURSEMENT_IN_PROGRESS, REIMBURSED, REJECTED
    }

    public enum Currency {
        CHF, EUR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Boolean active;
    private OffsetDateTime dateTime;
    private String name;
    private Double amount;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Enumerated(EnumType.STRING)
    private Currency currency;
    private OffsetDateTime serviceDateTime;
    private OffsetDateTime paidDateTime;
    private String provider;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    private Beneficiary beneficiary;

    @ManyToOne(fetch = FetchType.EAGER)
    private InsuranceSubmission submission;

}