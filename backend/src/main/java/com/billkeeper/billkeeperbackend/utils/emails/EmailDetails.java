package com.billkeeper.billkeeperbackend.utils.emails;

import lombok.*;

@Getter
@Setter
@Builder
public class EmailDetails {
    private String recipient;
    private String subject;
    private String body;
}