package com.billkeeper.billkeeperbackend.utils.accessmanager;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;

public class AccessManager {

    public static void checkIfUserCanAccessBillOrThrowException(User user, Bill bill) {
        if (!userCanAccessResourceCreatedByAuthor(user, bill.getUser())) {
            throw new UnauthorizedException("");
        }
    }

    public static void checkIfUserCanAccessDocumentOrThrowException(User user, Document document) {
        if (!userCanAccessResourceCreatedByAuthor(user, document.getUser())) {
            throw new UnauthorizedException("");
        }
    }

    public static void checkIfUserCanAccessSubmissionOrThrowException(User user, InsuranceSubmission submission) {
        if (!userCanAccessResourceCreatedByAuthor(user, submission.getUser())) {
            throw new UnauthorizedException("");
        }
    }

    public static boolean userCanAccessResourceCreatedByAuthor(User user, User author) {
        if (author.getId().equals(user.getId())) {
            return true;
        }
        return user.getFamily() != null &&
                author.getFamily() != null &&
                author.getFamily().getId().equals(user.getFamily().getId());
    }
}
