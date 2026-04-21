package com.securenotes.secure_notes_vault.service;

import com.securenotes.secure_notes_vault.dto.request.ShareAccessRequest;
import com.securenotes.secure_notes_vault.dto.request.ShareLinkRequest;
import com.securenotes.secure_notes_vault.dto.response.NoteResponse;
import com.securenotes.secure_notes_vault.dto.response.ShareLinkResponse;
import com.securenotes.secure_notes_vault.entity.Note;
import com.securenotes.secure_notes_vault.entity.ShareLink;
import com.securenotes.secure_notes_vault.entity.User;
import com.securenotes.secure_notes_vault.exception.BadRequestException;
import com.securenotes.secure_notes_vault.exception.ResourceNotFoundException;
import com.securenotes.secure_notes_vault.repository.NoteRepository;
import com.securenotes.secure_notes_vault.repository.ShareLinkRepository;
import com.securenotes.secure_notes_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareLinkService {

    private final ShareLinkRepository shareLinkRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    public ShareLinkResponse createShareLink(Long noteId,
                                             ShareLinkRequest request, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found"));

        Note note = noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + noteId));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now()
                .plusMinutes(request.getExpiryMinutes());

        ShareLink shareLink = ShareLink.builder()
                .token(token)
                .note(note)
                .expiryTime(expiryTime)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        shareLinkRepository.save(shareLink);

        return ShareLinkResponse.builder()
                .token(token)
                .shareUrl("http://localhost:8080/api/share/" + token)
                .expiryTime(expiryTime)
                .build();
    }

    public NoteResponse accessSharedNote(String token,
                                         ShareAccessRequest request) {

        ShareLink shareLink = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Share link not found or expired"));

        if (LocalDateTime.now().isAfter(shareLink.getExpiryTime())) {
            shareLinkRepository.delete(shareLink);
            throw new BadRequestException("Share link has expired");
        }

        if (!passwordEncoder.matches(request.getPassword(),
                shareLink.getPasswordHash())) {
            throw new BadRequestException("Invalid password");
        }

        Note note = shareLink.getNote();

        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(encryptionService.decrypt(note.getContent()))
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}