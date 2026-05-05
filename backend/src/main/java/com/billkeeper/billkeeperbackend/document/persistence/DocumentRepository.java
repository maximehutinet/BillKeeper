package com.billkeeper.billkeeperbackend.document.persistence;

import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends CrudRepository<Document, UUID> {

    List<Document> findByBillIdAndActiveTrue(UUID billId);

    @Query("SELECT d FROM Document d WHERE d.bill.id = :#{#billId} AND " +
            "(d.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND d.user.family IS NOT NULL AND d.user.family.id = :#{#user.family?.id})) AND " +
            "d.active IS TRUE")
    List<Document> findByBillIdAndActiveTrue(UUID billId, User user);

    @Modifying
    @Query("UPDATE Document d SET d.active = false WHERE d.bill.id = :billId AND d.active = true")
    void deactivateByBillId(@Param("billId") UUID billId);
}