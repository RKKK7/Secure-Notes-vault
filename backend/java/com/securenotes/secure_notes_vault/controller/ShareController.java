package com.securenotes.secure_notes_vault.controller;

import com.securenotes.secure_notes_vault.dto.request.ShareAccessRequest;
import com.securenotes.secure_notes_vault.dto.request.ShareLinkRequest;
import com.securenotes.secure_notes_vault.dto.response.ApiResponse;
import com.securenotes.secure_notes_vault.dto.response.NoteResponse;
import com.securenotes.secure_notes_vault.dto.response.ShareLinkResponse;
import com.securenotes.secure_notes_vault.service.ShareLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareLinkService shareLinkService;

    @PostMapping("/create/{noteId}")
    public ResponseEntity<ApiResponse<ShareLinkResponse>> createShareLink(
            @PathVariable Long noteId,
            @Valid @RequestBody ShareLinkRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ShareLinkResponse response = shareLinkService.createShareLink(
                noteId, request, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success("Share link created successfully",
                        response));
    }

    @PostMapping("/{token}/verify")
    public ResponseEntity<ApiResponse<NoteResponse>> accessSharedNote(
            @PathVariable String token,
            @Valid @RequestBody ShareAccessRequest request) {
        NoteResponse response = shareLinkService.accessSharedNote(
                token, request);
        return ResponseEntity.ok(
                ApiResponse.success("Note accessed successfully", response));
    }
}