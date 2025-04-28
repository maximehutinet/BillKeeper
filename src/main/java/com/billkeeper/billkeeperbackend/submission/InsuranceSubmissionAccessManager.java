package com.billkeeper.billkeeperbackend.submission;

import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.family.persistence.model.Family;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;

public class InsuranceSubmissionAccessManager {

    public static void checkIfUserCanAccessSubmissionOrThrowException(User user, InsuranceSubmission submission) {
        if (!userCanAccessSubmission(user, submission)) {
            throw new UnauthorizedException("");
        }
    }

    public static boolean userCanAccessSubmission(User user, InsuranceSubmission submission) {
        User submissionAuthor = submission.getUser();
        Family submissionAuthorFamily = submissionAuthor.getFamily();
        if (submissionAuthor.getId().equals(user.getId())) {
            return true;
        }
        return user.getFamily() != null &&
                submissionAuthorFamily != null &&
                submissionAuthorFamily.getId().equals(user.getFamily().getId());
    }
}