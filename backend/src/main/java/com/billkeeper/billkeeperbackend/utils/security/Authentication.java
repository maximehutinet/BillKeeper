package com.billkeeper.billkeeperbackend.utils.security;

import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class Authentication {

    private final UserRepository userRepository;

    public Authentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUserFromToken(JwtAuthenticationToken token) throws UnauthorizedException {
        return userRepository.findUserByKeycloakId(token.getName())
                .orElseThrow(() -> new UnauthorizedException(""));
    }
}