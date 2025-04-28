package com.billkeeper.billkeeperbackend.user.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.exception.UnauthorizedException;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final AppConfig appConfig;
    private final Authentication authentication;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, AppConfig appConfig, Authentication authentication) {
        this.userRepository = userRepository;
        this.appConfig = appConfig;
        this.authentication = authentication;
    }

    @GetMapping("/users/me")
    public UserResponse getCurrentUserProfile(JwtAuthenticationToken token) {
        User user = userRepository.findUserByKeycloakId(token.getName())
                .orElseThrow(() -> new UnauthorizedException(""));
        return new UserResponse(user);
    }

    @PostMapping("/users/me/picture")
    public void updateCurrentUserProfilePicture(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        try {
            User user = userRepository.findUserByKeycloakId(token.getName())
                    .orElseThrow(() -> new UnauthorizedException(""));
            String filename = UUID.randomUUID() + "." +  FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            Path destination = Paths.get(appConfig.getUsersDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            user.setProfilePictureName(filename);
            userRepository.save(user);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading picture");
        }
    }

    @GetMapping("/users/me/picture")
    public ResponseEntity<Resource> getCurrentUserProfilePicture(JwtAuthenticationToken token) {
        try {
            User user = userRepository.findUserByKeycloakId(token.getName())
                    .orElseThrow(() -> new UnauthorizedException(""));
            return buildProfilePictureResponse(user);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while fetching profile picture");
        }
    }

    @GetMapping("/users/{id}/picture")
    public ResponseEntity<Resource> getUserProfilePicture(@PathVariable UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            return buildProfilePictureResponse(user);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while fetching profile picture");
        }
    }

    @GetMapping("/users/suggestions")
    public List<UserResponse> getUsersStartingWith(@RequestParam("value") String value, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return userRepository.findAllUsersMatchingValue(value, user);
    }

    private ResponseEntity<Resource> buildProfilePictureResponse(User user) throws MalformedURLException {
        if (user.getProfilePictureName() == null || user.getProfilePictureName().isEmpty()) {
            return ResponseEntity.ok()
                    .build();
        }
        File profilePicture = new File(appConfig.getUsersDirectory() + File.separator + user.getProfilePictureName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new UrlResource(profilePicture.toURI()));
    }
}
