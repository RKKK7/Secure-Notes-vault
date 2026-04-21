package com.securenotes.secure_notes_vault.service;

import com.securenotes.secure_notes_vault.dto.request.NoteRequest;
import com.securenotes.secure_notes_vault.dto.response.NoteResponse;
import com.securenotes.secure_notes_vault.entity.Note;
import com.securenotes.secure_notes_vault.entity.User;
import com.securenotes.secure_notes_vault.exception.ResourceNotFoundException;
import com.securenotes.secure_notes_vault.exception.UnauthorizedException;
import com.securenotes.secure_notes_vault.repository.NoteRepository;
import com.securenotes.secure_notes_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));
    }

    public NoteResponse createNote(NoteRequest request, String username) {
        User user = getUser(username);

        Note note = Note.builder()
                .title(request.getTitle())
                .content(encryptionService.encrypt(request.getContent()))
                .user(user)
                .build();

        Note saved = noteRepository.save(note);
        return mapToResponse(saved);
    }

    public List<NoteResponse> getAllNotes(String username) {
        User user = getUser(username);
        return noteRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NoteResponse getNoteById(Long id, String username) {
        User user = getUser(username);
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + id));
        return mapToResponse(note);
    }

    public NoteResponse updateNote(Long id, NoteRequest request,
                                   String username) {
        User user = getUser(username);
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + id));

        note.setTitle(request.getTitle());
        note.setContent(encryptionService.encrypt(request.getContent()));

        Note updated = noteRepository.save(note);
        return mapToResponse(updated);
    }

    public void deleteNote(Long id, String username) {
        User user = getUser(username);
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Note not found with id: " + id));
        noteRepository.delete(note);
    }

    private NoteResponse mapToResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(encryptionService.decrypt(note.getContent()))
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}