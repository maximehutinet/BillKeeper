package com.billkeeper.billkeeperbackend.user.api.model;

import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String firstname;
    private String email;

    public UserResponse(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.email = user.getEmail();
    }
}