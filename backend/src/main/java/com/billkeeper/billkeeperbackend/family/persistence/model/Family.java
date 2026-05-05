package com.billkeeper.billkeeperbackend.family.persistence.model;

import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name= "family")
@Data
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @OneToOne
    private User owner;
}
