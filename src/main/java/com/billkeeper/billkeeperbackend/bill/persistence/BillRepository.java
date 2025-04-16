package com.billkeeper.billkeeperbackend.bill.persistence;

import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends CrudRepository<Bill, UUID> {
    @Query("SELECT new com.billkeeper.billkeeperbackend.bill.api.model.BillResponse(b, p.status) FROM Bill b JOIN ParsingJob p ON p.bill = b WHERE b.active is true ORDER BY b.dateTime DESC")
    List<BillResponse> findAllActiveBills();

    List<Bill> findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(UUID submissionId);

    Optional<Bill> findByIdAndSubmissionNull(UUID id);

    Integer countByActiveTrueAndStatus(Bill.Status status);

    List<Bill> findAllByActiveTrueAndStatus(Bill.Status status);

    List<Bill> findAllByActiveTrueAndPaidDateTimeNull();

    @Query("SELECT DISTINCT b.provider FROM Bill b WHERE lower(b.provider) LIKE lower(CONCAT(:provider, '%'))")
    List<String> findAllProvidersMatchingValue(String provider);
}