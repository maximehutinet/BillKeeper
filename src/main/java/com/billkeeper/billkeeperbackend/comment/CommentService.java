package com.billkeeper.billkeeperbackend.comment;

import com.billkeeper.billkeeperbackend.bill.BillService;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.comment.api.model.CommentResponse;
import com.billkeeper.billkeeperbackend.comment.api.model.CreateUpdateCommentRequest;
import com.billkeeper.billkeeperbackend.comment.persistence.CommentRepository;
import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BillService billService;
    private final CommentEmailNotifier commentEmailNotifier;

    public CommentService(CommentRepository commentRepository, BillService billService, CommentEmailNotifier commentEmailNotifier) {
        this.commentRepository = commentRepository;
        this.billService = billService;
        this.commentEmailNotifier = commentEmailNotifier;
    }

    public List<CommentResponse> getBillComments(UUID billId, User user) {
        billService.checkUserCanAccessBill(billId, user);
        return commentRepository.findByBillIdAndActiveTrueOrderByDateTimeDesc(billId);
    }

    @Transactional
    public void createComment(CreateUpdateCommentRequest request, Bill bill, User user) {
        Comment comment = new Comment();
        comment.setDateTime(OffsetDateTime.now());
        comment.setActive(true);
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setBill(bill);
        commentRepository.save(comment);
        commentEmailNotifier.notifyTaggedUsers(request, comment);
    }

    @Transactional
    public void updateComment(CreateUpdateCommentRequest request, UUID id, User user) {
        Comment comment = getCommentForUser(id, user);
        comment.setDateTime(OffsetDateTime.now());
        comment.setContent(request.getContent());
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(UUID id, User user) {
        Comment comment = getCommentForUser(id, user);
        comment.setActive(false);
        commentRepository.save(comment);
    }

    private Comment getCommentForUser(UUID id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to edit this comment");
        }
        return comment;
    }
}
