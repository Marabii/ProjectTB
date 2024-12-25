package com.projetTB.projetTB.Payments.CreatePaymentLink.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentRequest {
    @NotNull(message = "NoteId is required")
    private Long noteId;
}