package com.billkeeper.billkeeperbackend.family.api.model;

import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FamilyResponse {
    private String name;
    private List<UserResponse> members;
    private UserResponse owner;
}
