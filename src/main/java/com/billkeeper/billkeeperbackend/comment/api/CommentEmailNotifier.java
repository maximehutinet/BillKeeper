package com.billkeeper.billkeeperbackend.comment.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.comment.api.model.CreateUpdateCommentRequest;
import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.emails.EmailDetails;
import com.billkeeper.billkeeperbackend.utils.emails.EmailService;
import com.billkeeper.billkeeperbackend.utils.emails.EmailTemplates;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.stream.StreamSupport;

@Service
public class CommentEmailNotifier {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final AppConfig appConfig;

    public CommentEmailNotifier(UserRepository userRepository, EmailService emailService, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.appConfig = appConfig;
    }

    @Async
    public void notifyTaggedUsers(CreateUpdateCommentRequest request, Comment comment) {
        StreamSupport
                .stream(userRepository.findAllById(request.getTaggedUsersId()).spliterator(), false)
                .forEach(taggedUser -> sendNotificationEmailToUsers(comment, taggedUser));
    }

    private void sendNotificationEmailToUsers(Comment comment, User user) {
        EmailDetails details = EmailDetails
                .builder()
                .subject("You were tagged in a comment")
                .recipient(user.getEmail())
                .build();
        Context templateContext = new Context();
        templateContext.setVariable("name", user.getFirstname());
        templateContext.setVariable("commentAuthor", comment.getUser().getFirstname());
        templateContext.setVariable("billName", comment.getBill().getName());
        templateContext.setVariable("commentContent", comment.getContent());
        templateContext.setVariable("billLink", appConfig.getFrontUrl() + "/bill/" + comment.getBill().getId());
        this.emailService.sendEmailWithTemplate(details, templateContext, EmailTemplates.BILL_COMMENT_NOTIFICATIONS_TEMPLATE);
    }
}
