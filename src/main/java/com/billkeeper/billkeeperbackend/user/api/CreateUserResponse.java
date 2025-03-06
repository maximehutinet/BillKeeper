package com.billkeeper.billkeeperbackend.user.api;

import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.stereotype.Service;

@Service
public class CreateUserResponse {

    public UserResponse create(User user) {
        return UserResponse
                .builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .email(user.getEmail())
                .build();
    }
}