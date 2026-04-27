package com.billkeeper.billkeeperbackend.document.api;

import com.billkeeper.billkeeperbackend.document.DocumentService;
import com.billkeeper.billkeeperbackend.document.api.model.UpdateDocumentRequest;
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
public class DocumentController {
    private final Authentication authentication;
    private final DocumentService documentService;

    public DocumentController(Authentication authentication, DocumentService documentService) {
        this.authentication = authentication;
        this.documentService = documentService;
    }

    @PostMapping("/documents")
    public void createDocument(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        documentService.createDocument(multipartFile, user);
    }

    @GetMapping("/documents")
    public ResponseEntity<Resource> getMergedBillsDocuments(@RequestParam("billIds") List<UUID> billIds, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return documentService.getMergedDocuments(billIds, user);
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Resource> getDocument(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return documentService.getDocument(id, user);
    }

    @PostMapping("/documents/{id}")
    public void updateDocument(@RequestBody UpdateDocumentRequest request, @PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        documentService.updateDocument(id, request, user);
    }

    @DeleteMapping("/documents/{id}")
    public void delete(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        documentService.deleteDocument(id, user);
    }
}
