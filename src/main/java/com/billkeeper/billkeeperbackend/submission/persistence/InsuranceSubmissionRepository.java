package com.billkeeper.billkeeperbackend.submission.persistence;

import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface InsuranceSubmissionRepository extends CrudRepository<InsuranceSubmission, UUID> {

    @Query("SELECT s FROM InsuranceSubmission s " +
            "WHERE (s.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND s.user.family IS NOT NULL AND s.user.family.id = :#{#user.family?.id})) AND s.active IS TRUE " +
            "ORDER BY s.dateTime DESC")
    List<InsuranceSubmission> findAllByActiveTrueOrderByDateTimeDesc(User user);
}