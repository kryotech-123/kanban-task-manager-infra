package com.amalitech.kanbantaskmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for sending emails within the application.
 * It primarily handles sending One-Time Passwords (OTPs) for user authentication.
 * This service leverages Spring's {@link JavaMailSender} and operates asynchronously
 * to avoid blocking the main application thread during email delivery.
 */
@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * The email address from which OTP emails will be sent.
     * This value is injected from the application's properties file
     * using the 'app.api.otpsenderemail' key.
     */
    @Value("${app.api.otpsenderemail}")
    private String senderEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Asynchronously sends an OTP email to a specified recipient.
     * The method constructs a simple text email containing the OTP and sender information.
     * Email sending is performed on a separate thread, preventing it from blocking
     * the main request processing flow.
     *
     * @param to  The recipient's email address to whom the OTP should be sent.
     * @param otp The One-Time Password (OTP) code to be included in the email.
     * @implNote Although {@link MailException} is caught, it's logged rather than re-thrown
     * to ensure the core business flow isn't interrupted by email failures.
     * Consider more sophisticated error handling (e.g., retries, dead-letter queues)
     * for production systems if email delivery is critical.
     */
    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(to);
            message.setSubject("Your Kanban Login OTP");
            message.setText("Your One-Time Password (OTP) for Kanban login is: " + otp + "\n\n"
                    + "This OTP is valid for 5 minutes. Please do not share this with anyone.");
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (MailException e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage(), e);
        }
    }
}