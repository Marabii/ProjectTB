package com.projetTB.projetTB.Payments.CreatePaymentLink.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projetTB.projetTB.Payments.CreatePaymentLink.DTO.CreatePaymentRequest;
import com.projetTB.projetTB.Payments.CreatePaymentLink.DTO.CreatePaymentResponse;
import com.projetTB.projetTB.Payments.CreatePaymentLink.DTO.StripeResponse;
import com.projetTB.projetTB.Payments.CreatePaymentLink.Service.StripeService;

@RestController
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/api/protected/v1/stripe/create-payment")
    public ResponseEntity<StripeResponse<CreatePaymentResponse>> createPayment(
            @RequestBody @Valid CreatePaymentRequest createPaymentRequest, HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        StripeResponse<CreatePaymentResponse> stripeResponse = stripeService.createPayment(createPaymentRequest,
                userEmail);
        return ResponseEntity.status(stripeResponse.getHttpStatus()).body(stripeResponse);
    }
}