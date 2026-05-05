package com.billkeeper.billkeeperbackend.comment.api.model;

import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class CommentResponse {
    private UUID id;
    private OffsetDateTime dateTime;
    private String content;
    private UserResponse user;
    private BillResponse bill;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.dateTime = comment.getDateTime();
        this.content = comment.getContent();
        this.user = new UserResponse(comment.getUser());
        this.bill = new BillResponse(comment.getBill());
    }
}
