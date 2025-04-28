package com.billkeeper.billkeeperbackend.bill.persistence;

import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends CrudRepository<Bill, UUID> {
    @Query("SELECT new com.billkeeper.billkeeperbackend.bill.api.model.BillResponse(b, p.status) " +
            "FROM Bill b " +
            "LEFT JOIN ParsingJob p ON p.bill = b " +
            "WHERE (b.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND b.user.family IS NOT NULL AND b.user.family.id = :#{#user.family?.id})) AND b.active IS TRUE " +
            "ORDER BY b.dateTime DESC")
    List<BillResponse> findAllActiveBills(User user);

    @Query("SELECT new com.billkeeper.billkeeperbackend.bill.api.model.BillResponse(b, p.status) " +
            "FROM Bill b " +
            "LEFT JOIN ParsingJob p ON p.bill = b " +
            "WHERE b.id = :#{#id} AND " +
            "(b.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND b.user.family IS NOT NULL AND b.user.family.id = :#{#user.family?.id})) AND " +
            "b.active IS TRUE")
    Optional<BillResponse> findBillById(UUID id, User user);

    List<Bill> findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(UUID submissionId);

    Optional<Bill> findByIdAndSubmissionNull(UUID id);

    @Query("SELECT COUNT (b) FROM Bill b " +
            "WHERE (b.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND b.user.family IS NOT NULL AND b.user.family.id = :#{#user.family?.id})) AND " +
            "b.status = :#{#status} AND " +
            "b.active IS TRUE")
    Integer countByActiveTrueAndStatus(Bill.Status status, User user);

    @Query("SELECT b FROM Bill b " +
            "WHERE (b.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND b.user.family IS NOT NULL AND b.user.family.id = :#{#user.family?.id})) AND " +
            "b.status = :#{#status} AND " +
            "b.active IS TRUE")
    List<Bill> findAllByActiveTrueAndStatus(Bill.Status status, User user);

    @Query("SELECT b FROM Bill b " +
            "WHERE (b.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND b.user.family IS NOT NULL AND b.user.family.id = :#{#user.family?.id})) AND " +
            "b.paidDateTime IS NULL AND " +
            "b.active IS TRUE")
    List<Bill> findAllByActiveTrueAndPaidDateTimeNull(User user);

    @Query("SELECT DISTINCT b.provider FROM Bill b WHERE lower(b.provider) LIKE lower(CONCAT(:provider, '%'))")
    List<String> findAllProvidersMatchingValue(String provider);
}