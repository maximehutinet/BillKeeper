package com.billkeeper.billkeeperbackend.user.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class UserResponse {
    private UUID id;
    private String firstname;
    private String email;
}