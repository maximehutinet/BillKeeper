package com.billkeeper.billkeeperbackend.stats.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatsResponse {
    private Integer totalActiveBills;
}
