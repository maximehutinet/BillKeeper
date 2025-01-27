package com.billkeeper.billkeeperbackend.bill.persistence.model;

import com.billkeeper.billkeeperbackend.beneficiary.model.Beneficiary;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bill")
@Data
public class Bill {

    public enum Status {
        TO_FILE, FILED, REIMBURSED
    }

    public enum Currency {
        CHF, EURO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Boolean active;
    private OffsetDateTime dateTime;
    private String name;
    private Double amount;
    private Currency currency;
    private OffsetDateTime paidDateTime;
    private String provider;
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    private Beneficiary beneficiary;
}