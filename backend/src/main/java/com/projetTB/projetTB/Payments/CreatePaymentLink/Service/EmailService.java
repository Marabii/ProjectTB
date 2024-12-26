package com.projetTB.projetTB.Payments.CreatePaymentLink.Service;

import com.projetTB.projetTB.Email.DTO.EmailRequest;
import com.projetTB.projetTB.Email.service.EmailSenderService;
import com.projetTB.projetTB.Notes.models.Note;
import com.projetTB.projetTB.Notes.models.NoteFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class EmailService {

    private final EmailSenderService emailSenderService;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(EmailSenderService emailSenderService, TemplateEngine templateEngine) {
        this.emailSenderService = emailSenderService;
        this.templateEngine = templateEngine;
    }

    public void sendEmails(Note note, String buyerEmail) throws Exception {
        Context context = new Context();
        context.setVariable("note", note);
        context.setVariable("buyerName", "Customer");

        String subject = "Thank You for Your Purchase!";

        if (note.isDigital()) {
            // For digital purchases, include document links
            List<NoteFile> files = note.getFiles();
            context.setVariable("files", files);

            // Process the digital email template
            String htmlContent = templateEngine.process("email/digital-email", context);

            // Create EmailRequest
            EmailRequest emailRequest = EmailRequest.builder().recipient(buyerEmail).subject(subject)
                    .message(htmlContent).build();

            // Send the email
            emailSenderService.sendEmail(emailRequest);

        } else {
            // For physical purchases, include secret password
            String secretPassword = note.getSecretPassword();
            context.setVariable("secretPassword", secretPassword);

            // Process the physical email template
            String htmlContent = templateEngine.process("email/physical-email", context);

            // Create EmailRequest
            EmailRequest emailRequest = EmailRequest.builder().recipient(buyerEmail).subject(subject)
                    .message(htmlContent).build();

            // Send the email
            emailSenderService.sendEmail(emailRequest);
        }
    }

    /**
     * Sends a failure notification email to the customer when the original email
     * fails to send.
     *
     * @param note       The Note object related to the purchase.
     * @param buyerEmail The email address of the buyer.
     * @param buyerName  The name of the buyer.
     * @throws Exception If sending the failure notification email fails.
     */
    public void sendEmailFailureNotification(Note note, String buyerEmail) throws Exception {
        Context context = new Context();
        context.setVariable("noteId", note.getId());
        context.setVariable("noteTitle", note.getTitle());
        context.setVariable("buyerName", "Customer");

        String subject = "Issue with Your Recent Purchase";

        // Process the failure notification email template
        String htmlContent = templateEngine.process("email/failure-notification", context);

        // Create EmailRequest
        EmailRequest emailRequest = EmailRequest.builder().recipient(buyerEmail).subject(subject).message(htmlContent)
                .build();

        // Send the failure notification email
        emailSenderService.sendEmail(emailRequest);
    }
}