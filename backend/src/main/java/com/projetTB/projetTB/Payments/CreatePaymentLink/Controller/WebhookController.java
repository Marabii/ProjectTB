package com.projetTB.projetTB.Payments.CreatePaymentLink.Controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.projetTB.projetTB.Auth.exceptions.UserNotFoundException;
import com.projetTB.projetTB.Auth.models.Users;
import com.projetTB.projetTB.Email.DTO.EmailRequest;
import com.projetTB.projetTB.Email.service.EmailSenderService;
import com.projetTB.projetTB.Notes.Service.NotesService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final EmailSenderService emailSenderService;
    private final NotesService notesService;

    @Value("${stripe.webhookKey}")
    private String webhookKey;

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<Void> webhook(@RequestBody String payload,
            @RequestHeader("stripe-signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookKey);
        } catch (SignatureVerificationException e) {
            System.out.println("Failed signature verification");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Use the deserializer provided by the Event object
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, handle the raw JSON data
            String rawJson = dataObjectDeserializer.getRawJson().toString(); // Convert JsonElement to String if needed
            // Attempt to parse the JSON string to a PaymentIntent object if necessary
            if ("payment_intent.succeeded".equals(event.getType())) {
                stripeObject = PaymentIntent.GSON.fromJson(rawJson, PaymentIntent.class);
            }
        }

        switch (event.getType()) {
        case "payment_intent.succeeded":
            if (stripeObject instanceof PaymentIntent paymentIntent) {
                String buyerEmail = paymentIntent.getMetadata().get("buyerEmail");
                Long noteId = Long.parseLong(paymentIntent.getMetadata().get("noteId"));

                try {
                    notesService.grantAccessToNoteFile(noteId, buyerEmail);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // sendEmails200(noteId);
            }
            break;
        case "payment_method.attached":
            if (stripeObject instanceof PaymentMethod) {
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                System.out.println("Payment Method Attached: " + paymentMethod);
            }
            break;
        // Handle other event types
        default:
            System.out.println("Unhandled event type: " + event.getType());
            break;
        }
        return ResponseEntity.noContent().build();
    }

    // private void sendEmails200(String orderId) {
    // Orders order = ordersRepository.findById(orderId)
    // .orElseThrow(() -> new OrderNotFoundException("Could not find order " +
    // orderId));
    // Users traveler = usersRepository.findById(order.getTravelerId())
    // .orElseThrow(() -> new UserNotFoundException("Could not find traveler"));
    // Users buyer = usersRepository.findById(order.getBuyerId())
    // .orElseThrow(() -> new UserNotFoundException("Could not find buyer"));

    // try {
    // // Prepare placeholders for the traveler email
    // Map<String, String> travelerPlaceholders = new HashMap<>();
    // travelerPlaceholders.put("travelerName", traveler.getName());
    // travelerPlaceholders.put("actualValue",
    // String.valueOf(order.getActualValue()));
    // travelerPlaceholders.put("deliveryFee",
    // String.valueOf(order.getActualDeliveryFee()));
    // travelerPlaceholders.put("orderId", order.get_id());
    // travelerPlaceholders.put("productName", order.getProductName());
    // travelerPlaceholders.put("description", order.getDescription());
    // // Add more placeholders as needed

    // // Generate email content for the traveler
    // String travelerEmailContent = getEmailContent("traveler_email_template.html",
    // travelerPlaceholders);

    // // Create and send the email to the traveler
    // EmailRequest travelerEmailRequest =
    // EmailRequest.builder().recipient(traveler.getEmail())
    // .subject("Order Transaction
    // Completed").message(travelerEmailContent).build();

    // emailSenderService.sendEmail(travelerEmailRequest);

    // // Prepare placeholders for the buyer email
    // Map<String, String> buyerPlaceholders = new HashMap<>();
    // buyerPlaceholders.put("buyerName", buyer.getName());
    // buyerPlaceholders.put("actualValue", String.valueOf(order.getActualValue()));
    // buyerPlaceholders.put("orderId", order.get_id());
    // buyerPlaceholders.put("productName", order.getProductName());
    // buyerPlaceholders.put("description", order.getDescription());
    // // Add more placeholders as needed

    // // Generate email content for the buyer
    // String buyerEmailContent = getEmailContent("buyer_email_template.html",
    // buyerPlaceholders);

    // // Create and send the email to the buyer
    // EmailRequest buyerEmailRequest =
    // EmailRequest.builder().recipient(buyer.getEmail())
    // .subject("Transaction Successful").message(buyerEmailContent).build();

    // emailSenderService.sendEmail(buyerEmailRequest);

    // } catch (IOException e) {
    // // Handle exceptions (e.g., log the error)
    // e.printStackTrace();
    // }
    // }

    // private String getEmailContent(String templateFileName, Map<String, String>
    // placeholders) throws IOException {
    // // Load the email template from the resources/templates/ directory
    // ClassLoader classLoader = getClass().getClassLoader();
    // InputStream inputStream = classLoader.getResourceAsStream("templates/" +
    // templateFileName);
    // if (inputStream == null) {
    // throw new FileNotFoundException("Template file not found: " +
    // templateFileName);
    // }
    // String template = new String(inputStream.readAllBytes(),
    // StandardCharsets.UTF_8);

    // // Replace placeholders with actual values
    // for (Map.Entry<String, String> entry : placeholders.entrySet()) {
    // template = template.replace("${" + entry.getKey() + "}", entry.getValue());
    // }

    // return template;
    // }

}
