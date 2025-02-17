package com.billkeeper.billkeeperbackend.settings.persistence;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "settings")
@Data
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    private OffsetDateTime dateTime;
    private Boolean active;
}
