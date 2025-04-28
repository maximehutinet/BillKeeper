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

    @Query("SELECT new com.billkeeper.billkeeperbackend.user.api.model.UserResponse(u) " +
            "FROM User u " +
            "WHERE lower(u.firstname) LIKE lower(CONCAT(:value, '%')) AND " +
            "(u.id != :#{#user.id} AND (:#{#user.family?.id} IS NOT NULL AND u.family IS NOT NULL AND u.family.id = :#{#user.family?.id}))")
    List<UserResponse> findAllUsersMatchingValue(String value, User user);
}