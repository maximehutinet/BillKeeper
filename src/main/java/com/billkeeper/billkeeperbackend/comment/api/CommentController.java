package com.billkeeper.billkeeperbackend.comment.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.comment.api.model.CreateUpdateCommentRequest;
import com.billkeeper.billkeeperbackend.comment.persistence.CommentRepository;
import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {

    private final CommentRepository commentRepository;
    private final BillRepository billRepository;

    public CommentController(CommentRepository commentRepository, BillRepository billRepository) {
        this.commentRepository = commentRepository;
        this.billRepository = billRepository;
    }

    @GetMapping("/comments")
    public List<Comment> getBillComments(@RequestParam UUID billId) {
        if (!billRepository.existsById(billId)) {
            throw new NotFoundException("Bill not found");
        }
        return commentRepository.findByBillIdAndActiveTrueOrderByDateTimeDesc(billId);
    }

    @PostMapping("/comments")
    public void createComment(@RequestBody CreateUpdateCommentRequest request, @RequestParam UUID billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        Comment comment = new Comment();
        comment.setDateTime(OffsetDateTime.now());
        comment.setActive(true);
        comment.setContent(request.getContent());
        comment.setBill(bill);
        commentRepository.save(comment);
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