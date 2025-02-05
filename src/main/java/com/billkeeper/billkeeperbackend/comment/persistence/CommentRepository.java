package com.billkeeper.billkeeperbackend.comment.persistence;

import com.billkeeper.billkeeperbackend.comment.persistence.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends CrudRepository<Comment, UUID> {

    List<Comment> findByBillIdAndActiveTrueOrderByDateTimeDesc(UUID billId);
}
