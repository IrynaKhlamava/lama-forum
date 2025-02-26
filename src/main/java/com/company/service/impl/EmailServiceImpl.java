package com.company.service.impl;

import com.company.model.enumType.RoleName;
import com.company.service.EmailService;
import com.company.service.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = createMimeMessage(to, subject, body);
            mailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
            throw new EmailSendingException("Failed to send email to " + to, e);
        }
    }

    @Override
    public void prepareAndSendEmail(String email, String token, RoleName role) {
        String link = buildLink(token, role);
        String subject;
        String emailBody;

        if (role == RoleName.USER) {
            subject = "Activate Your Account";
            emailBody = buildActivationEmailBody(link);
        } else {
            subject = "Admin Invitation";
            emailBody = buildInvitationEmailBody(link);
        }

        sendEmail(email, subject, emailBody);
        logger.info("{} email sent to {}", subject, email);

    }

    private String buildLink(String token, RoleName roleName) {
        String path = (roleName == RoleName.ADMIN) ? "/admins/set-admin-account" : "/users/activate";
        return String.format("%s%s?token=%s", appUrl, path, token);
    }

    private String buildActivationEmailBody(String activationLink) {
        return String.format(
                "<p>Hello,</p>" +
                        "<p>Click the link below to activate your account:</p>" +
                        "<p><a href='%s'>Activate Account</a></p>" +
                        "<p>If you didn't request this, please ignore this email</p>",
                activationLink
        );
    }

    private String buildInvitationEmailBody(String link) {
        return String.format(
                "<p>Hello,</p>" +
                        "<p>You have been invited as an admin. Click the link below to set your password:</p>" +
                        "<p><a href='%s'>Set Admin Password</a></p>" +
                        "<p>If you didn't request this, please ignore this email</p>",
                link
        );
    }

    private MimeMessage createMimeMessage(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        return message;
    }

}