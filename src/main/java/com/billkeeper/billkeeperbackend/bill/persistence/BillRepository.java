package com.billkeeper.billkeeperbackend.bill.persistence;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BillRepository extends CrudRepository<Bill, UUID> {
}