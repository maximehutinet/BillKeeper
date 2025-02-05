package com.billkeeper.billkeeperbackend.comment.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUpdateCommentRequest {
    private String content;
}