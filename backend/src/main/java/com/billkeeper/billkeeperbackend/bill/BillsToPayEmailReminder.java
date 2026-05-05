package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.emails.EmailDetails;
import com.billkeeper.billkeeperbackend.utils.emails.EmailService;
import com.billkeeper.billkeeperbackend.utils.emails.EmailTemplates;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class BillsToPayEmailReminder {

    private final AppConfig appConfig;

    private final EmailService emailService;

    public BillsToPayEmailReminder(AppConfig appConfig, EmailService emailService) {
        this.appConfig = appConfig;
        this.emailService = emailService;
    }

    @Async
    public void sendReminderEmail(User user, List<Bill> bills) {
        EmailDetails details = EmailDetails
                .builder()
                .subject("Make in rain, some bills are waiting for you!")
                .recipient(user.getEmail())
                .build();
        Context templateContext = new Context();
        templateContext.setVariable("name", user.getFirstname());
        templateContext.setVariable("billsToPay", buildBillsToPayHtml(bills));
        this.emailService.sendEmailWithTemplate(details, templateContext, EmailTemplates.BILLS_TO_PAY_REMINDER_TEMPLATE);
    }

    private String buildBillsToPayHtml(List<Bill> bills) {
        StringBuilder billsToPayHtml = new StringBuilder();
        bills.forEach(bill -> {
            String billName = bill.getName() != null ? bill.getName() : "N/S";
            String billAmount = bill.getAmount() != null ? bill.getAmount() + bill.getCurrency().toString() : "Amount N/S";
            String billLink = appConfig.getFrontUrl() + "/bill/" + bill.getId();
            String html = String.format("<a href=\"%s\">%s - %s</a>", billLink, billName, billAmount);
            billsToPayHtml.append(html);
        });
        return billsToPayHtml.toString();
    }
}