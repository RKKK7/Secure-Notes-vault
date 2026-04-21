package com.securenotes.secure_notes_vault.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShareAccessRequest {

    @NotBlank(message = "Password is required")
    private String password;
}