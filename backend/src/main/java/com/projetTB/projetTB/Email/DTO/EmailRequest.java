package com.projetTB.projetTB.Email.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class EmailRequest {
    @Email
    @NotNull(message = "recipient is required")
    private String recipient;

    @NotNull(message = "message is required")
    private String message;

    @NotNull(message = "subject is required")
    private String subject;
}
