package com.securenotes.secure_notes_vault.controller;

import com.securenotes.secure_notes_vault.dto.request.NoteRequest;
import com.securenotes.secure_notes_vault.dto.response.ApiResponse;
import com.securenotes.secure_notes_vault.dto.response.NoteResponse;
import com.securenotes.secure_notes_vault.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> createNote(
            @Valid @RequestBody NoteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        NoteResponse response = noteService.createNote(
                request, userDetails.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Note created successfully",
                        response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getAllNotes(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<NoteResponse> notes = noteService.getAllNotes(
                userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success("Notes fetched successfully", notes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponse>> getNoteById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        NoteResponse response = noteService.getNoteById(
                id, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success("Note fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponse>> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        NoteResponse response = noteService.updateNote(
                id, request, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success("Note updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        noteService.deleteNote(id, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success("Note deleted successfully", null));
    }
}