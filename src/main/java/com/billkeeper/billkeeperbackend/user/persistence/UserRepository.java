package com.billkeeper.billkeeperbackend.user.persistence;

import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Boolean existsUserByKeycloakId(String keycloakId);
    Optional<User> findUserByKeycloakId(String keycloakId);
}