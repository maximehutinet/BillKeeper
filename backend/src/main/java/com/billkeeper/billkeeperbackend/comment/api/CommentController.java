package com.billkeeper.billkeeperbackend.comment.api;

import com.billkeeper.billkeeperbackend.bill.BillService;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.comment.CommentService;
import com.billkeeper.billkeeperbackend.comment.api.model.CommentResponse;
import com.billkeeper.billkeeperbackend.comment.api.model.CreateUpdateCommentRequest;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.security.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {
    private final CommentService commentService;
    private final Authentication authentication;
    private final BillService billService;


    public CommentController(CommentService commentService, Authentication authentication, BillService billService) {
        this.commentService = commentService;
        this.authentication = authentication;
        this.billService = billService;
    }

    @GetMapping("/comments")
    public List<CommentResponse> getBillComments(@RequestParam UUID billId, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        billService.checkUserCanAccessBill(billId, user);
        return commentService.getBillComments(billId, user);
    }

    @PostMapping("/comments")
    public void createComment(@RequestBody CreateUpdateCommentRequest request, @RequestParam UUID billId, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(billId, user);
        commentService.createComment(request, bill, user);
    }

    @PostMapping("/comments/{id}")
    public void updateComment(@RequestBody CreateUpdateCommentRequest request, @PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        commentService.updateComment(request, id, user);
    }

    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        commentService.deleteComment(id, user);
    }
}