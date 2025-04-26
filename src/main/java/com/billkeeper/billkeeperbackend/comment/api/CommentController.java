package com.billkeeper.billkeeperbackend.comment.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.comment.CommentEmailNotifier;
import com.billkeeper.billkeeperbackend.comment.api.model.CommentResponse;
import com.billkeeper.billkeeperbackend.comment.api.model.CreateUpdateCommentRequest;
import com.billkeeper.billkeeperbackend.comment.persistence.CommentRepository;
import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {

    private final CommentRepository commentRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final CommentEmailNotifier commentEmailNotifier;


    public CommentController(CommentRepository commentRepository, BillRepository billRepository, UserRepository userRepository, CommentEmailNotifier commentEmailNotifier) {
        this.commentRepository = commentRepository;
        this.billRepository = billRepository;
        this.userRepository = userRepository;
        this.commentEmailNotifier = commentEmailNotifier;
    }

    @GetMapping("/comments")
    public List<CommentResponse> getBillComments(@RequestParam UUID billId) {
        if (!billRepository.existsById(billId)) {
            throw new NotFoundException("Bill not found");
        }
        return commentRepository.findByBillIdAndActiveTrueOrderByDateTimeDesc(billId);
    }

    @PostMapping("/comments")
    public void createComment(@RequestBody CreateUpdateCommentRequest request, @RequestParam UUID billId, JwtAuthenticationToken jwtAuthenticationToken) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                .orElseThrow(() -> new UnauthorizedException(""));
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
    public void updateComment(@RequestBody CreateUpdateCommentRequest request, @PathVariable UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.setDateTime(OffsetDateTime.now());
        comment.setContent(request.getContent());
        commentRepository.save(comment);
    }

    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.setActive(false);
        commentRepository.save(comment);
    }

}