package com.billkeeper.billkeeperbackend.submission.persistence;

import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface InsuranceSubmissionRepository extends CrudRepository<InsuranceSubmission, UUID> {
    List<InsuranceSubmission> findAllByActiveTrueOrderByDateTimeDesc();
}