package com.billkeeper.billkeeperbackend.comment.persistence.model;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    private OffsetDateTime dateTime;
    private Boolean active;
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne
    private Bill bill;

}