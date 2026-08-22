package com.securenotes.secure_notes_vault.controller;

import com.securenotes.secure_notes_vault.dto.response.AdminNoteResponse;
import com.securenotes.secure_notes_vault.dto.response.ApiResponse;
import com.securenotes.secure_notes_vault.dto.response.UserResponse;
import com.securenotes.secure_notes_vault.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // GET /api/admin/users → list all users
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.success("All users fetched successfully", users));
    }

    // GET /api/admin/notes → list all notes (decrypted) across all users
    @GetMapping("/notes")
    public ResponseEntity<ApiResponse<List<AdminNoteResponse>>> getAllNotes() {
        List<AdminNoteResponse> notes = adminService.getAllNotes();
        return ResponseEntity.ok(
                ApiResponse.success("All notes fetched successfully", notes));
    }

    // DELETE /api/admin/users/{id} → delete any user (non-admin only)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully", null));
    }

    // DELETE /api/admin/notes/{id} → delete any note
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNote(@PathVariable Long id) {
        adminService.deleteNote(id);
        return ResponseEntity.ok(
                ApiResponse.success("Note deleted successfully", null));
    }

    // PUT /api/admin/users/{id}/promote → promote user to ADMIN
    @PutMapping("/users/{id}/promote")
    public ResponseEntity<ApiResponse<UserResponse>> promoteUser(@PathVariable Long id) {
        UserResponse user = adminService.promoteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User promoted to ADMIN successfully", user));
    }

    // PUT /api/admin/users/{id}/demote → demote ADMIN to USER
    @PutMapping("/users/{id}/demote")
    public ResponseEntity<ApiResponse<UserResponse>> demoteUser(@PathVariable Long id) {
        UserResponse user = adminService.demoteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User demoted to USER successfully", user));
    }
}
