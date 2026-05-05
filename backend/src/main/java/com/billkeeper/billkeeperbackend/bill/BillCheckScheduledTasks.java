package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.family.persistence.model.Family;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BillCheckScheduledTasks {

    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final BillsToPayEmailReminder billsToPayEmailReminder;

    public BillCheckScheduledTasks(BillRepository billRepository, UserRepository userRepository, BillsToPayEmailReminder billsToPayEmailReminder) {
        this.billRepository = billRepository;
        this.userRepository = userRepository;
        this.billsToPayEmailReminder = billsToPayEmailReminder;
    }

    @Scheduled(cron = "0 0 10 */2 * *")
    public void checkForUnpaidBills() {
        List<Bill> unpaidBills = billRepository.findBillsByActiveTrueAndPaidDateTimeNull();
        Map<UUID, List<Bill>> familyBills = new HashMap<>();
        Map<UUID, List<Bill>> userBills = new HashMap<>();

        unpaidBills.forEach(unpaidBill -> {
            User user = unpaidBill.getUser();
            Family family = user.getFamily();
            if (family != null) {
                familyBills
                        .computeIfAbsent(family.getId(), k -> new ArrayList<>())
                        .add(unpaidBill);
            } else {
                userBills
                        .computeIfAbsent(user.getId(), k -> new ArrayList<>())
                        .add(unpaidBill);
            }
        });

        familyBills.forEach((familyId, bills) -> {
            List<User> users = userRepository.findAllUsersWithFamilyId(familyId);
            users.forEach(user -> billsToPayEmailReminder.sendReminderEmail(user, bills));
        });

        userBills.forEach((userId, bills) -> {
            User user = bills.getFirst().getUser();
            billsToPayEmailReminder.sendReminderEmail(user, bills);
        });

    }
}
