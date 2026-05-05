package com.billkeeper.billkeeperbackend.infra.security;

import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationListener {

    private final UserRepository userRepository;

    public JwtAuthenticationListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            createUserIfNotExists(jwtAuthenticationToken);
        }
    }

    private void createUserIfNotExists(JwtAuthenticationToken jwtAuthenticationToken) {
        try {
            String keycloakId = jwtAuthenticationToken.getTokenAttributes().get("sub").toString();
            String email = jwtAuthenticationToken.getTokenAttributes().get("email").toString();
            String firstname = jwtAuthenticationToken.getTokenAttributes().get("given_name").toString();
            if (!userRepository.existsUserByKeycloakId(keycloakId)) {
                User user = new User();
                user.setKeycloakId(keycloakId);
                user.setEmail(email);
                user.setFirstname(firstname);
                userRepository.save(user);
            }
        } catch (Exception ignored) { }
    }
}