package com.projetTB.projetTB.Payments.CreatePaymentLink.Controller;

import com.projetTB.projetTB.Notes.Repository.NoteRepository;
import com.projetTB.projetTB.Notes.Service.NotesService;
import com.projetTB.projetTB.Notes.models.Note;
import com.projetTB.projetTB.Payments.CreatePaymentLink.Service.EmailService;
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

import java.util.Random;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final EmailService emailService;
    private final NotesService notesService;
    private final NoteRepository noteRepository;

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
                    Note note = noteRepository.findById(noteId)
                            .orElseThrow(() -> new IllegalStateException("Document not found"));
                    if (note.isDigital())
                        notesService.grantAccessToNoteFile(noteId, buyerEmail);
                    else {
                        String documentsPassword = generatePassword();
                        note.setSecretPassword(documentsPassword);
                        noteRepository.save(note);
                    }

                    // Attempt to send the purchase confirmation email
                    try {
                        emailService.sendEmails(note, buyerEmail);
                    } catch (Exception emailEx) {
                        System.err.println("Failed to send purchase confirmation email: " + emailEx.getMessage());
                        // Attempt to send failure notification email to the customer
                        try {
                            emailService.sendEmailFailureNotification(note, buyerEmail);
                        } catch (Exception failureEx) {
                            System.err.println("Failed to send failure notification email: " + failureEx.getMessage());
                            // Optionally, log this failure or notify support through other means
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // Optionally, handle other exceptions or notify support
                }
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

    public static String generatePassword() {
        char[] letters = { 'A', 'B', 'C' };
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) { // Increased password length for better security
            result.append(letters[random.nextInt(letters.length)]);
        }

        return result.toString();
    }
}
