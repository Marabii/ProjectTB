package com.projetTB.projetTB.Payments.CreatePaymentLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreatePaymentResponse {
    private String sessionId;
    private String sessionUrl;
}
