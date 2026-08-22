package com.securenotes.secure_notes_vault.service;

import com.securenotes.secure_notes_vault.dto.response.AdminNoteResponse;
import com.securenotes.secure_notes_vault.dto.response.UserResponse;
import com.securenotes.secure_notes_vault.entity.Note;
import com.securenotes.secure_notes_vault.entity.User;
import com.securenotes.secure_notes_vault.exception.BadRequestException;
import com.securenotes.secure_notes_vault.exception.ResourceNotFoundException;
import com.securenotes.secure_notes_vault.repository.NoteRepository;
import com.securenotes.secure_notes_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final EncryptionService encryptionService;

    // ── Get all users ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole().name())
                        .noteCount(user.getNotes() != null ? user.getNotes().size() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    // ── Get all notes (decrypted) across all users ─────────────────
    @Transactional(readOnly = true)
    public List<AdminNoteResponse> getAllNotes() {
        return noteRepository.findAll()
                .stream()
                .map(this::mapToAdminNoteResponse)
                .collect(Collectors.toList());
    }

    // ── Delete any user by ID ──────────────────────────────────────
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        if (user.getRole() == User.Role.ADMIN) {
            throw new BadRequestException("Cannot delete an ADMIN user");
        }
        userRepository.delete(user);
    }

    // ── Delete any note by ID ──────────────────────────────────────
    public void deleteNote(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + noteId));
        noteRepository.delete(note);
    }

    // ── Promote user to ADMIN ──────────────────────────────────────
    public UserResponse promoteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        if (user.getRole() == User.Role.ADMIN) {
            throw new BadRequestException("User is already an ADMIN");
        }
        user.setRole(User.Role.ADMIN);
        userRepository.save(user);
        return buildUserResponse(user);
    }

    // ── Demote ADMIN to USER ───────────────────────────────────────
    public UserResponse demoteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId));
        if (user.getRole() == User.Role.USER) {
            throw new BadRequestException("User is already a regular USER");
        }
        user.setRole(User.Role.USER);
        userRepository.save(user);
        return buildUserResponse(user);
    }

    // ── Helpers ────────────────────────────────────────────────────
    private AdminNoteResponse mapToAdminNoteResponse(Note note) {
        return AdminNoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(encryptionService.decrypt(note.getContent()))
                .owner(note.getUser().getUsername())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .noteCount(user.getNotes() != null ? user.getNotes().size() : 0)
                .build();
    }
}
