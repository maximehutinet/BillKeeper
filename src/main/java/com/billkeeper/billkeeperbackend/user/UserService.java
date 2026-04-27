package com.billkeeper.billkeeperbackend.user;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AppConfig appConfig;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.appConfig = appConfig;
    }

    public UserResponse getCurrentUserProfile(User user) {
        return new UserResponse(user);
    }

    public void updateProfilePicture(User user, MultipartFile multipartFile) {
        try {
            String filename = UUID.randomUUID() + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            Path destination = Paths.get(appConfig.getUsersDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            user.setProfilePictureName(filename);
            userRepository.save(user);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading picture");
        }
    }

    public ResponseEntity<Resource> getProfilePicture(User user) {
        try {
            return buildProfilePictureResponse(user);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while fetching profile picture");
        }
    }

    public ResponseEntity<Resource> getProfilePictureById(UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            return buildProfilePictureResponse(user);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while fetching profile picture");
        }
    }

    public List<UserResponse> getUsersMatchingValue(String value, User user) {
        return userRepository.findAllUsersMatchingValue(value, user);
    }

    private ResponseEntity<Resource> buildProfilePictureResponse(User user) throws MalformedURLException {
        if (user.getProfilePictureName() == null || user.getProfilePictureName().isEmpty()) {
            return ResponseEntity.ok().build();
        }
        File profilePicture = new File(appConfig.getUsersDirectory() + File.separator + user.getProfilePictureName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new UrlResource(profilePicture.toURI()));
    }
}
