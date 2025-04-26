package com.billkeeper.billkeeperbackend.family.api;

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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class FamilyController {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;

    private final InvitationEmailNotifier invitationEmailNotifier;

    public FamilyController(FamilyRepository familyRepository, UserRepository userRepository, InvitationRepository invitationRepository, InvitationEmailNotifier invitationEmailNotifier) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.invitationEmailNotifier = invitationEmailNotifier;
    }

    @GetMapping("/family/me")
    public FamilyResponse getMyFamily(JwtAuthenticationToken jwtAuthenticationToken) {
        User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                .orElseThrow(() -> new UnauthorizedException(""));
        if (user.getFamily() == null) {
            return null;
        }
        return FamilyResponse
                .builder()
                .name(user.getFamily().getName())
                .owner(new UserResponse(user.getFamily().getOwner()))
                .members(familyRepository.findAllMembersByFamilyId(user.getFamily().getId()))
                .build();
    }

    @PostMapping("/family")
    public void createFamily(@RequestBody CreateUpdateFamilyRequest request, JwtAuthenticationToken jwtAuthenticationToken) {
        User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                .orElseThrow(() -> new UnauthorizedException(""));
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new BadRequestException("Family name cannot be empty");
        }
        Family family = new Family();
        family.setName(request.getName());
        family.setOwner(user);
        familyRepository.save(family);
        user.setFamily(family);
        userRepository.save(user);
    }

    @PostMapping("/family/members")
    public void addMemberToFamily(@RequestBody AddMemberToFamilyRequest request, JwtAuthenticationToken jwtAuthenticationToken) {
        User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                .orElseThrow(() -> new UnauthorizedException(""));
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new BadRequestException("Email address cannot be empty");
        }
        Invitation invitation = createJoinFamilyInvitation(user, request.getEmail());
        invitationEmailNotifier.sendJoinFamilyInvitationEmail(invitation, user.getFamily());
    }

    @PostMapping("/family/invitation/{invitationId}")
    public void acceptFamilyInvitation(@PathVariable("invitationId") UUID invitationId, JwtAuthenticationToken jwtAuthenticationToken) {
        User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                .orElseThrow(() -> new UnauthorizedException(""));
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new NotFoundException("Invitation not found"));
        if (!user.getEmail().equals(invitation.getRecipientEmail())) {
            throw new UnauthorizedException("");
        }
        Family family = invitation.getAuthor().getFamily();
        user.setFamily(family);
        userRepository.save(user);
        invitation.setStatus(Invitation.Status.ACCEPTED);
        invitationRepository.save(invitation);
    }

    private Invitation createJoinFamilyInvitation(User currentUser, String email) {
        Invitation invitation = new Invitation();
        invitation.setDateTime(OffsetDateTime.now());
        invitation.setActive(true);
        invitation.setAuthor(currentUser);
        invitation.setRecipientEmail(email);
        invitation.setType(Invitation.Type.JOIN_FAMILY);
        invitation.setStatus(Invitation.Status.PENDING);
        invitationRepository.save(invitation);
        return invitation;
    }
}
