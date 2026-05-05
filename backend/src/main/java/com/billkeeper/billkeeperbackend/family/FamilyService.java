package com.billkeeper.billkeeperbackend.family;

import com.billkeeper.billkeeperbackend.exception.BadRequestException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.family.api.model.AddMemberToFamilyRequest;
import com.billkeeper.billkeeperbackend.family.api.model.CreateUpdateFamilyRequest;
import com.billkeeper.billkeeperbackend.family.api.model.FamilyResponse;
import com.billkeeper.billkeeperbackend.family.persistence.FamilyRepository;
import com.billkeeper.billkeeperbackend.family.persistence.model.Family;
import com.billkeeper.billkeeperbackend.invitation.InvitationEmailNotifier;
import com.billkeeper.billkeeperbackend.invitation.persistence.InvitationRepository;
import com.billkeeper.billkeeperbackend.invitation.persistence.model.Invitation;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final InvitationEmailNotifier invitationEmailNotifier;

    public FamilyService(FamilyRepository familyRepository, UserRepository userRepository, InvitationRepository invitationRepository, InvitationEmailNotifier invitationEmailNotifier) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.invitationEmailNotifier = invitationEmailNotifier;
    }

    public FamilyResponse getMyFamily(User user) {
        if (user.getFamily() == null) {
            return null;
        }
        return FamilyResponse.builder()
                .name(user.getFamily().getName())
                .owner(new UserResponse(user.getFamily().getOwner()))
                .members(familyRepository.findAllMembersByFamilyId(user.getFamily().getId()))
                .build();
    }

    @Transactional
    public void createFamily(CreateUpdateFamilyRequest request, User user) {
        if (user.getFamily() != null) {
            throw new BadRequestException("User already belongs to a family");
        }
        Family family = new Family();
        family.setName(request.getName());
        family.setOwner(user);
        familyRepository.save(family);
        user.setFamily(family);
        userRepository.save(user);
    }

    @Transactional
    public void addMemberToFamily(AddMemberToFamilyRequest request, User user) {
        if (user.getFamily() == null) {
            throw new BadRequestException("You must belong to a family to invite members");
        }
        if (!user.getId().equals(user.getFamily().getOwner().getId())) {
            throw new UnauthorizedException("Only the family owner can invite members");
        }
        Invitation invitation = createInvitation(user, request.getEmail());
        invitationEmailNotifier.sendJoinFamilyInvitationEmail(invitation, user.getFamily());
    }

    @Transactional
    public void acceptFamilyInvitation(UUID invitationId, User user) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotFoundException("Invitation not found"));
        if (user.getFamily() != null) {
            throw new BadRequestException("User already belongs to a family");
        }
        if (!user.getEmail().equals(invitation.getRecipientEmail())) {
            throw new UnauthorizedException("");
        }
        if (invitation.getStatus() != Invitation.Status.PENDING) {
            throw new BadRequestException("Invitation is no longer valid");
        }
        user.setFamily(invitation.getAuthor().getFamily());
        userRepository.save(user);
        invitation.setStatus(Invitation.Status.ACCEPTED);
        invitationRepository.save(invitation);
    }

    private Invitation createInvitation(User user, String email) {
        Invitation invitation = new Invitation();
        invitation.setDateTime(OffsetDateTime.now());
        invitation.setActive(true);
        invitation.setAuthor(user);
        invitation.setRecipientEmail(email);
        invitation.setType(Invitation.Type.JOIN_FAMILY);
        invitation.setStatus(Invitation.Status.PENDING);
        invitationRepository.save(invitation);
        return invitation;
    }
}
