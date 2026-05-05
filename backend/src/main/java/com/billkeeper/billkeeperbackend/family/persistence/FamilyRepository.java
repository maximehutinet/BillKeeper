package com.billkeeper.billkeeperbackend.family.persistence;

import com.billkeeper.billkeeperbackend.family.persistence.model.Family;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface FamilyRepository extends CrudRepository<Family, UUID> {

    @Query("SELECT new com.billkeeper.billkeeperbackend.user.api.model.UserResponse(u) FROM User u WHERE u.family.id = ?1")
    List<UserResponse> findAllMembersByFamilyId(UUID familyId);
}
