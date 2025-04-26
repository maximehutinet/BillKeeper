package com.billkeeper.billkeeperbackend.invitation;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.family.persistence.model.Family;
import com.billkeeper.billkeeperbackend.invitation.persistence.model.Invitation;
import com.billkeeper.billkeeperbackend.utils.emails.EmailDetails;
import com.billkeeper.billkeeperbackend.utils.emails.EmailService;
import com.billkeeper.billkeeperbackend.utils.emails.EmailTemplates;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class InvitationEmailNotifier {

    private final AppConfig appConfig;

    private final EmailService emailService;

    public InvitationEmailNotifier(AppConfig appConfig, EmailService emailService) {
        this.appConfig = appConfig;
        this.emailService = emailService;
    }

    @Async
    public void sendJoinFamilyInvitationEmail(Invitation invitation, Family family) {
        EmailDetails details = EmailDetails
                .builder()
                .subject("You are invited to join " + family.getName())
                .recipient(invitation.getRecipientEmail())
                .build();
        String acceptationLink = String.format("%s/invitation/family/%s", appConfig.getFrontUrl(), invitation.getId());
        Context templateContext = new Context();
        templateContext.setVariable("invitationAuthor", invitation.getAuthor().getFirstname());
        templateContext.setVariable("familyName", family.getName());
        templateContext.setVariable("acceptationLink", acceptationLink);
        this.emailService.sendEmailWithTemplate(details, templateContext, EmailTemplates.JOIN_FAMILY_INVITATION_TEMPLATE);
    }
}
