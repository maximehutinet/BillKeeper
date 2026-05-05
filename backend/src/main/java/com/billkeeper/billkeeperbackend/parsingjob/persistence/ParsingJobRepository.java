package com.billkeeper.billkeeperbackend.parsingjob.persistence;

import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ParsingJobRepository extends CrudRepository<ParsingJob, UUID> {
}