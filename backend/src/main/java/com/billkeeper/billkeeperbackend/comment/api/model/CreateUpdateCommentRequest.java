package com.billkeeper.billkeeperbackend.comment.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateUpdateCommentRequest {
    private String content;
    private List<UUID> taggedUsersId;
}