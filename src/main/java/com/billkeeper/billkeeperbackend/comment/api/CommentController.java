package com.billkeeper.billkeeperbackend.comment.api;

import com.billkeeper.billkeeperbackend.bill.BillAccessManager;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.comment.CommentEmailNotifier;
import com.billkeeper.billkeeperbackend.comment.api.model.CommentResponse;
import com.billkeeper.billkeeperbackend.comment.api.model.CreateUpdateCommentRequest;
import com.billkeeper.billkeeperbackend.comment.persistence.CommentRepository;
import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {

    private final CommentRepository commentRepository;
    private final BillRepository billRepository;
    private final CommentEmailNotifier commentEmailNotifier;
    private final Authentication authentication;


    public CommentController(CommentRepository commentRepository, BillRepository billRepository, CommentEmailNotifier commentEmailNotifier, Authentication authentication) {
        this.commentRepository = commentRepository;
        this.billRepository = billRepository;
        this.commentEmailNotifier = commentEmailNotifier;
        this.authentication = authentication;
    }

    @GetMapping("/comments")
    public List<CommentResponse> getBillComments(@RequestParam UUID billId, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        BillAccessManager.checkIfUserCanAccessBillOrThrowException(user, bill);
        return commentRepository.findByBillIdAndActiveTrueOrderByDateTimeDesc(billId);
    }

    @PostMapping("/comments")
    public void createComment(@RequestBody CreateUpdateCommentRequest request, @RequestParam UUID billId, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        BillAccessManager.checkIfUserCanAccessBillOrThrowException(user, bill);
        Comment comment = new Comment();
        comment.setDateTime(OffsetDateTime.now());
        comment.setActive(true);
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setBill(bill);
        commentRepository.save(comment);
        commentEmailNotifier.notifyTaggedUsers(request, comment);
    }

    @PostMapping("/comments/{id}")
    public void updateComment(@RequestBody CreateUpdateCommentRequest request, @PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        checkIfUserCanEditCommentOrThrowException(user, comment);
        comment.setDateTime(OffsetDateTime.now());
        comment.setContent(request.getContent());
        commentRepository.save(comment);
    }

    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        checkIfUserCanEditCommentOrThrowException(user, comment);
        comment.setActive(false);
        commentRepository.save(comment);
    }

    private void checkIfUserCanEditCommentOrThrowException(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to edit this comment");
        }
    }

}