package com.billkeeper.billkeeperbackend.user.persistence.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name= "billkeeperuser")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String keycloakId;
    private String firstname;
    private String email;
}