package com.billkeeper.billkeeperbackend.user.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.apache.commons.io.FilenameUtils;
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
import java.util.UUID;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final AppConfig appConfig;
    private final CreateUserResponse createUserResponse;

    public UserController(UserRepository userRepository, AppConfig appConfig, CreateUserResponse createUserResponse) {
        this.userRepository = userRepository;
        this.appConfig = appConfig;
        this.createUserResponse = createUserResponse;
    }

    @GetMapping("/users/me")
    public UserResponse getCurrentUserProfile(JwtAuthenticationToken jwtAuthenticationToken) {
        User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return createUserResponse.create(user);
    }

    @PostMapping("/users/me/picture")
    public void updateCurrentUserProfilePicture(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken jwtAuthenticationToken) {
        try {
            User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            String filename = UUID.randomUUID() + "." +  FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            Path destination = Paths.get(appConfig.getUsersDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            user.setProfilePictureName(filename);
            userRepository.save(user);
        } catch (IOException | RuntimeException e) {
            throw new InternalServerErrorException("Error while uploading picture");
        }
    }

    @GetMapping("/users/me/picture")
    public ResponseEntity<Resource> getCurrentUserProfilePicture(JwtAuthenticationToken jwtAuthenticationToken) {
        try {
            User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            return buildProfilePictureResponse(user);
        } catch (IOException e) {
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
            throw new InternalServerErrorException("Error while fetching profile picture");
        }
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
