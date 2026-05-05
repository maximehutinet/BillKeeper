package com.billkeeper.billkeeperbackend.bill.persistence;

import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.stats.StatsProjection;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.db.QueryConstants;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends CrudRepository<Bill, UUID> {
    @Query("SELECT new com.billkeeper.billkeeperbackend.bill.api.model.BillResponse(b, p.status) " +
            "FROM Bill b " +
            "LEFT JOIN ParsingJob p ON p.bill = b " +
            "WHERE " + QueryConstants.USER_OR_FAMILY_MEMBER_FILTER + " AND b.active IS TRUE " +
            "ORDER BY b.dateTime DESC")
    List<BillResponse> findAllActiveBills(User user);

    @Query("SELECT new com.billkeeper.billkeeperbackend.bill.api.model.BillResponse(b, p.status) " +
            "FROM Bill b " +
            "LEFT JOIN ParsingJob p ON p.bill = b " +
            "WHERE b.id = :#{#id} AND " +
            QueryConstants.USER_OR_FAMILY_MEMBER_FILTER + " AND b.active IS TRUE")
    Optional<BillResponse> findBillById(UUID id, User user);

    List<Bill> findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(UUID submissionId);

    Optional<Bill> findByIdAndSubmissionNull(UUID id);

    @Query("SELECT DISTINCT b.provider " +
            "FROM Bill b " +
            "WHERE lower(b.provider) LIKE lower(CONCAT(:provider, '%')) AND " +
            QueryConstants.USER_OR_FAMILY_MEMBER_FILTER)
    List<String> findAllProvidersMatchingValue(String provider, User user);

    List<Bill> findBillsByActiveTrueAndPaidDateTimeNull();

    @Query(value =
            "SELECT " +
                    "  SUM(CASE WHEN b.paid_date_time IS NULL THEN b.amount ELSE 0 END) as totalToPay, " +
                    "  SUM(CASE WHEN b.status IN ('TO_FILE', 'FILED') THEN b.amount ELSE 0 END) as totalToBeReimbursed, " +
                    "  SUM(CASE WHEN b.status = 'TO_FILE' THEN 1 ELSE 0 END) as toFileCount, " +
                    "  SUM(CASE WHEN b.status = 'FILED' THEN 1 ELSE 0 END) as inProgressCount " +
                    "FROM bill b " +
                    "JOIN billkeeperuser u ON u.id = b.user_id " +
                    "WHERE (b.user_id = :userId OR (:familyId IS NOT NULL AND u.family_id IS NOT NULL AND u.family_id = :familyId)) " +
                    "AND b.active = TRUE",
            nativeQuery = true)
    StatsProjection getStats(@Param("userId") UUID userId, @Param("familyId") UUID familyId);
}