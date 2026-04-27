package com.billkeeper.billkeeperbackend.submission.api;

import com.billkeeper.billkeeperbackend.submission.InsuranceSubmissionService;
import com.billkeeper.billkeeperbackend.submission.api.model.CreateUpdateInsuranceSubmissionRequest;
import com.billkeeper.billkeeperbackend.submission.api.model.InsuranceSubmissionResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class InsuranceSubmissionController {
    private final Authentication authentication;
    private final InsuranceSubmissionService insuranceSubmissionService;

    public InsuranceSubmissionController(Authentication authentication, InsuranceSubmissionService insuranceSubmissionService) {
        this.authentication = authentication;
        this.insuranceSubmissionService = insuranceSubmissionService;
    }

    @GetMapping("submissions")
    public List<InsuranceSubmissionResponse> getAllActiveSubmissions(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return insuranceSubmissionService.getAllActiveSubmissions(user);
    }

    @GetMapping("submissions/{id}")
    public InsuranceSubmissionResponse getSubmission(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return insuranceSubmissionService.getSubmission(id, user);
    }

    @PostMapping("/submissions")
    public void createSubmission(@Valid @RequestBody CreateUpdateInsuranceSubmissionRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        insuranceSubmissionService.createSubmission(request, user);
    }

    @PostMapping("/submissions/{id}")
    public void updateSubmission(@PathVariable UUID id, @RequestBody CreateUpdateInsuranceSubmissionRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        insuranceSubmissionService.updateSubmission(id, request, user);
    }

    @DeleteMapping("submissions/{id}")
    public void deleteSubmission(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        insuranceSubmissionService.deleteSubmission(id, user);
    }

}