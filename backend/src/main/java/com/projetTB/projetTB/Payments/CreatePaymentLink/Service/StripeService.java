package com.projetTB.projetTB.Payments.CreatePaymentLink.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.projetTB.projetTB.Auth.exceptions.UserNotFoundException;
import com.projetTB.projetTB.Auth.repository.UsersRepository;
import com.projetTB.projetTB.Notes.Repository.NoteRepository;
import com.projetTB.projetTB.Notes.models.Note;
import com.projetTB.projetTB.Payments.CreatePaymentLink.DTO.CreatePaymentRequest;
import com.projetTB.projetTB.Payments.CreatePaymentLink.DTO.CreatePaymentResponse;
import com.projetTB.projetTB.Payments.CreatePaymentLink.DTO.StripeResponse;
import com.projetTB.projetTB.Payments.CreatePaymentLink.Enums.CURRENCY_CODE;
import com.stripe.model.checkout.Session;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {
        private final NoteRepository noteRepository;
        private final UsersRepository usersRepository;

        @Value("${stripe.secretKey}")
        private String secretKey;

        @Value("${front_end}")
        private String front_end;

        @Value("${successful_payment_link}")
        private String successful_payment_link;

        @Value("${failed_payment_link}")
        private String failed_payment_link;

        public StripeResponse<CreatePaymentResponse> createPayment(CreatePaymentRequest createPaymentRequest,
                        String userEmail) {
                Stripe.apiKey = secretKey;

                Note note = noteRepository.findById(createPaymentRequest.getNoteId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Either the note Id is wrong or the note itself was deleted"));

                if (!usersRepository.existsByEmail(userEmail))
                        throw new UserNotFoundException("You don't have an account yet");

                if (note.getAuthorizedUsers().stream().map(user -> user.getEmail()).toList().contains(userEmail))
                        throw new IllegalStateException("You have already purchased this item");

                if (!note.isAvailable())
                        throw new IllegalStateException("The documents aren't available yet");

                // Calculating totalCost
                Double totalCost = note.getPrice();

                // Create a PaymentIntent with the order amount and currency
                SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData
                                .builder().setName(note.getTitle()).build();

                // Create new line item with the above product data and associated price
                SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currencyEnumToString(CURRENCY_CODE.EUR))
                                .setUnitAmount((long) (totalCost * 100L)).setProductData(productData).build();

                // Create new line item with the above price data
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder().setQuantity((long) 1)
                                .setPriceData(priceData).build();

                // **Add metadata to PaymentIntentData**
                Map<String, String> metadata = new HashMap<>();
                metadata.put("noteId", Long.toString(createPaymentRequest.getNoteId()));
                metadata.put("buyerEmail", userEmail);

                // Create new session with the line items and set the capture method to
                // automatic
                SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(front_end + successful_payment_link)
                                .setCancelUrl(front_end + failed_payment_link)
                                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().setCaptureMethod(
                                                SessionCreateParams.PaymentIntentData.CaptureMethod.AUTOMATIC)
                                                .putAllMetadata(metadata) // **Add metadata here**
                                                .build())
                                .addLineItem(lineItem).build();

                // Create new session
                Session session;
                try {
                        session = Session.create(params);
                } catch (StripeException e) {
                        return StripeResponse.<CreatePaymentResponse>builder().status("FAILURE")
                                        .message("Payment session creation failed: " + e.getMessage()).httpStatus(400)
                                        .data(null).build();
                }

                CreatePaymentResponse responseData = CreatePaymentResponse.builder().sessionId(session.getId())
                                .sessionUrl(session.getUrl()).build();

                return StripeResponse.<CreatePaymentResponse>builder().status("SUCCESS")
                                .message("Payment session created successfully").httpStatus(200).data(responseData)
                                .build();
        }

        // helper function, turns currency codes from enum types to lowercase strings
        private String currencyEnumToString(CURRENCY_CODE currencyCode) {
                return currencyCode.name().toLowerCase();
        }

}
