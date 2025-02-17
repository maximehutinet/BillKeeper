package com.billkeeper.billkeeperbackend.settings.persistence;

import com.billkeeper.billkeeperbackend.settings.persistence.model.Settings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SettingsRepository extends CrudRepository<Settings, UUID> {

    @Query("SELECT s FROM Settings s WHERE s.active IS TRUE ORDER BY s.dateTime DESC LIMIT 1")
    Optional<Settings> findLatestSettings();
}