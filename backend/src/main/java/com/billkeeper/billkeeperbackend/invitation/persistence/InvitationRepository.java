package com.billkeeper.billkeeperbackend.invitation.persistence;

import com.billkeeper.billkeeperbackend.invitation.persistence.model.Invitation;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface InvitationRepository extends CrudRepository<Invitation, UUID> {
}
