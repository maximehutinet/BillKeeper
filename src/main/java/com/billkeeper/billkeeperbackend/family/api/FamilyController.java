package com.billkeeper.billkeeperbackend.family.api;

import com.billkeeper.billkeeperbackend.family.FamilyService;
import com.billkeeper.billkeeperbackend.family.api.model.AddMemberToFamilyRequest;
import com.billkeeper.billkeeperbackend.family.api.model.CreateUpdateFamilyRequest;
import com.billkeeper.billkeeperbackend.family.api.model.FamilyResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class FamilyController {

    private final FamilyService familyService;
    private final Authentication authentication;

    public FamilyController(FamilyService familyService, Authentication authentication) {
        this.familyService = familyService;
        this.authentication = authentication;
    }

    @GetMapping("/family/me")
    public FamilyResponse getMyFamily(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return familyService.getMyFamily(user);
    }

    @PostMapping("/family")
    public void createFamily(@Valid @RequestBody CreateUpdateFamilyRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        familyService.createFamily(request, user);
    }

    @PostMapping("/family/members")
    public void addMemberToFamily(@Valid @RequestBody AddMemberToFamilyRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        familyService.addMemberToFamily(request, user);
    }

    @PostMapping("/family/invitation/{invitationId}")
    public void acceptFamilyInvitation(@PathVariable UUID invitationId, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        familyService.acceptFamilyInvitation(invitationId, user);
    }
}
