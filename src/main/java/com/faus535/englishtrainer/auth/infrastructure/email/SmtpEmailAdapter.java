package com.faus535.englishtrainer.auth.infrastructure.email;

import com.faus535.englishtrainer.auth.domain.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
class SmtpEmailAdapter implements EmailPort {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailAdapter.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String frontendUrl;

    SmtpEmailAdapter(JavaMailSender mailSender,
                     @Value("${mail.from:noreply@englishtrainer.com}") String fromEmail,
                     @Value("${mail.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetUrl = frontendUrl + "/auth/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("English Trainer - Reset your password");
        message.setText(
                "Hi,\n\n"
                + "You requested a password reset. Click the link below to set a new password:\n\n"
                + resetUrl + "\n\n"
                + "This link expires in 15 minutes.\n\n"
                + "If you didn't request this, you can safely ignore this email.\n\n"
                + "- English Trainer Team"
        );

        try {
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }
}
