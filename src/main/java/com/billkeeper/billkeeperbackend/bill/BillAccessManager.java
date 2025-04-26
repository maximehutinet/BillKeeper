package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.family.persistence.model.Family;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;

public class BillAccessManager {

    public static void checkIfUserCanAccessBillOrThrowException(User user, Bill bill) {
        if (!userCanAccessBill(user, bill)) {
            throw new UnauthorizedException("You do not have permission to access the bill");
        }
    }

    public static boolean userCanAccessBill(User user, Bill bill) {
        User billAuthor = bill.getUser();
        Family billAuthorFamily = billAuthor.getFamily();
        if (billAuthor.getId().equals(user.getId())) {
            return true;
        }
        return user.getFamily() != null &&
                billAuthorFamily != null &&
                billAuthorFamily.getId().equals(user.getFamily().getId());
    }
}
