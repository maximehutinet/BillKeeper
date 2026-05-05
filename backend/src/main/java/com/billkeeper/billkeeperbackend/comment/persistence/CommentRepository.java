package com.billkeeper.billkeeperbackend.comment.persistence;

import com.billkeeper.billkeeperbackend.comment.api.model.CommentResponse;
import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends CrudRepository<Comment, UUID> {
    @Query("SELECT new com.billkeeper.billkeeperbackend.comment.api.model.CommentResponse(c) FROM Comment c WHERE c.bill.id = ?1 AND c.active ORDER BY c.dateTime DESC")
    List<CommentResponse> findByBillIdAndActiveTrueOrderByDateTimeDesc(UUID billId);
}
