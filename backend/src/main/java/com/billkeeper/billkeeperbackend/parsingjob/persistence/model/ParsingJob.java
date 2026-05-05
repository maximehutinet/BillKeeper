package com.billkeeper.billkeeperbackend.parsingjob.persistence.model;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "parsingjob")
@Data
public class ParsingJob {

    public enum Status {
        IN_PROGRESS, SUCCESS, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private OffsetDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    private Bill bill;

}