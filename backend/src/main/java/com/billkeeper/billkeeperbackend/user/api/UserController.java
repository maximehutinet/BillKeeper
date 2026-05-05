package com.billkeeper.billkeeperbackend.user.api;

import com.billkeeper.billkeeperbackend.user.UserService;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.security.Authentication;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;
    private final Authentication authentication;

    public UserController(UserService userService, Authentication authentication) {
        this.userService = userService;
        this.authentication = authentication;
    }

    @GetMapping("/users/me")
    public UserResponse getCurrentUserProfile(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return userService.getCurrentUserProfile(user);
    }

    @PostMapping("/users/me/picture")
    public void updateCurrentUserProfilePicture(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        userService.updateProfilePicture(user, multipartFile);
    }

    @GetMapping("/users/me/picture")
    public ResponseEntity<Resource> getCurrentUserProfilePicture(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return userService.getProfilePicture(user);
    }

    @GetMapping("/users/{id}/picture")
    public ResponseEntity<Resource> getUserProfilePicture(@PathVariable UUID id) {
        return userService.getProfilePictureById(id);
    }

    @GetMapping("/users/suggestions")
    public List<UserResponse> getUsersStartingWith(@RequestParam("value") String value, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return userService.getUsersMatchingValue(value, user);
    }
}
