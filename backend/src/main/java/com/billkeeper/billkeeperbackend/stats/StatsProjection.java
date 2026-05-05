package com.billkeeper.billkeeperbackend.stats;

public interface StatsProjection {
    Double getTotalToPay();
    Double getTotalToBeReimbursed();
    Integer getToFileCount();
    Integer getInProgressCount();
}