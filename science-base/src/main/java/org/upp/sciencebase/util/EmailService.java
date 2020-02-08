package org.upp.sciencebase.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.Text;
import org.upp.sciencebase.model.User;

import javax.mail.internet.MimeMessage;
import java.util.Base64;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String mailUsername;
    private static final String CLIENT_URL = "http://localhost:4200";

    @Autowired
    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String username) {
        this.mailSender = mailSender;
        this.mailUsername = username;
    }

    @Async
    public void sendNewTextNotificationMail(User user, Text text) {
        log.info("Sending email to user: {}", user.getUsername());
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, false, "utf-8");

            helper.setTo(user.getEmail());
            helper.setFrom(mailUsername);
            helper.setSubject("Science Base - New Text Notification");
            String message = "Hello " + user.getUsername() + ",<br><br>"
                    + "New text '" + text.getTitle() + "' has been submitted for magazine '" + text.getMagazine().getName()
                    + "' by author: " + user.getFullName() + ".<br><br>"
                    + "Regards, <br><br>" + "<i>Science Base Team</i>";
            helper.setText(message, true);
            mailSender.send(mail);

            log.info("Email sent to user! (username: {}, email: {})", user.getUsername(), user.getEmail());
        } catch (Exception e) {
            log.error("Error while sending email to user! (username: {}, email: {})", user.getUsername(), user.getEmail());
        }
    }

    @Async
    public void sendRegistrationMail(User user, String processInstanceId) {
        log.info("Sending email to user: {}", user.getUsername());
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, false, "utf-8");

            helper.setTo(user.getEmail());
            helper.setFrom(mailUsername);
            helper.setSubject("Science Base - Account Activation");
            String message = "Hello " + user.getUsername() + ",<br><br>"
                    + "Thank you for registering your Science Base account. To finally activate your account please "
                    + "<a href=\"" + getActivationCode(processInstanceId) + "\">click here.</a><br><br>"
                    + "Regards, <br><br>" + "<i>Science Base Team</i>";
            helper.setText(message, true);
            mailSender.send(mail);

            log.info("Email sent to user! (username: {}, email: {})", user.getUsername(), user.getEmail());
        } catch (Exception e) {
            log.error("Error while sending email to user! (username: {}, email: {})", user.getUsername(), user.getEmail());
        }
    }

    private String getActivationCode(String processInstanceId) {
        String code = Base64.getEncoder().withoutPadding().encodeToString(processInstanceId.getBytes());
        return CLIENT_URL.concat("/verify/").concat(code);
    }
}
