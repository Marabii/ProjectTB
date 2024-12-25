package com.projetTB.projetTB.Payments.CreatePaymentLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeResponse<T> {
    private String status;
    private String message;
    private Integer httpStatus;
    private T data;
}