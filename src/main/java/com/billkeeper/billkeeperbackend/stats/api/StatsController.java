package com.billkeeper.billkeeperbackend.stats.api;

import com.billkeeper.billkeeperbackend.stats.StatsService;
import com.billkeeper.billkeeperbackend.stats.api.model.StatsResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private final StatsService statsService;
    private final Authentication authentication;

    public StatsController(StatsService statsService, Authentication authentication) {
        this.statsService = statsService;
        this.authentication = authentication;
    }

    @GetMapping("/stats/bills")
    public StatsResponse getBillStats(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return statsService.getBillStats(user);
    }
}
