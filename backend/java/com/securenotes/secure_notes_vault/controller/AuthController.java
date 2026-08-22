package com.securenotes.secure_notes_vault.controller;

import com.securenotes.secure_notes_vault.dto.request.LoginRequest;
import com.securenotes.secure_notes_vault.dto.request.RegisterRequest;
import com.securenotes.secure_notes_vault.dto.response.ApiResponse;
import com.securenotes.secure_notes_vault.dto.response.AuthResponse;
import com.securenotes.secure_notes_vault.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    // POST /api/auth/register → Register as regular USER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    // POST /api/auth/register/admin?adminSecret=xxx → Register as ADMIN
    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<AuthResponse>> registerAdmin(
            @Valid @RequestBody RegisterRequest request,
            @RequestParam String adminSecret) {
        AuthResponse response = authService.registerAdmin(request, adminSecret);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registered successfully", response));
    }

    // POST /api/auth/login → Login (USER or ADMIN)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }
}
