package com.billkeeper.billkeeperbackend.utils.security.accessmanager;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.stereotype.Component;

@Component
public class AccessManager {

    public void checkIfUserCanAccessBillOrThrowException(User user, Bill bill) {
        checkAccess(user, bill.getUser());
    }

    public void checkIfUserCanAccessDocumentOrThrowException(User user, Document document) {
        checkAccess(user, document.getUser());
    }

    public void checkIfUserCanAccessSubmissionOrThrowException(User user, InsuranceSubmission submission) {
        checkAccess(user, submission.getUser());
    }

    public boolean userCanAccessResourceCreatedByAuthor(User user, User author) {
        if (author.getId().equals(user.getId())) {
            return true;
        }
        return user.getFamily() != null &&
                author.getFamily() != null &&
                author.getFamily().getId().equals(user.getFamily().getId());
    }

    private void checkAccess(User user, User resourceOwner) {
        if (!userCanAccessResourceCreatedByAuthor(user, resourceOwner)) {
            throw new UnauthorizedException("You are not authorized to access this resource");
        }
    }
}