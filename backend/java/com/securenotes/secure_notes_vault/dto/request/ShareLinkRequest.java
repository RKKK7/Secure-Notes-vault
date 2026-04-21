package com.securenotes.secure_notes_vault.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShareLinkRequest {

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Expiry minutes is required")
    private Integer expiryMinutes;
}