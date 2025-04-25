package com.billkeeper.billkeeperbackend.user.persistence;

import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Boolean existsUserByKeycloakId(String keycloakId);
    Optional<User> findUserByKeycloakId(String keycloakId);

    @Query("SELECT new com.billkeeper.billkeeperbackend.user.api.model.UserResponse(u.id, u.firstname, u.email) FROM User u WHERE lower(u.firstname) LIKE lower(CONCAT(:value, '%'))")
    List<UserResponse> findAllUsersMatchingValue(String value);
}