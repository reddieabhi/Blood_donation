package bloodfinders.blood_api.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class EmailService {

    @Value("${email.user}")
    private String username;

    @Value("${email.pass}")
    private String password;

    @Value("${email.smtp.host}")
    private String smtpHost;

    @Value("${email.smtp.port}")
    private int smtpPort;

    private final Properties props = new Properties();

    @PostConstruct
    public void init() {
        if (username == null || password == null) {
            throw new IllegalStateException("Email credentials must be set in application-dev.yml");
        }

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
    }




    public void sendEmail(String to, String subject, String body) throws MessagingException {
        sendEmailInternal(List.of(to), subject, body);
    }

    public void sendEmail(List<String> recipients, String subject, String body) throws MessagingException {
        sendEmailInternal(recipients, subject, body);
    }

    private void sendEmailInternal(List<String> recipients, String subject, String body) throws MessagingException {
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        Address[] recipientAddresses = recipients.stream()
                .map(email -> {
                    try {
                        return new InternetAddress(email);
                    } catch (AddressException e) {
                        throw new RuntimeException("Invalid email: " + email, e);
                    }
                })
                .toArray(Address[]::new);

        message.setRecipients(Message.RecipientType.TO, recipientAddresses);
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}
