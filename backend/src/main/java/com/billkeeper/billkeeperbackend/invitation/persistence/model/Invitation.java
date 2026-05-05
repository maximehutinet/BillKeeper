package com.billkeeper.billkeeperbackend.invitation.persistence.model;

import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name= "invitation")
@Data
public class Invitation {

    public enum Type {
        JOIN_FAMILY
    }

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private OffsetDateTime dateTime;
    private Boolean active;

    @OneToOne
    private User author;
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

}
