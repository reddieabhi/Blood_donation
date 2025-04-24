package bloodfinders.blood_api.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.List;
import java.util.Properties;

public class EmailService {

    private final String username;
    private final String password;
    private final Properties props;

    public EmailService(String smtpHost, int smtpPort) {
        this.username = System.getenv("EMAIL_USER");
        this.password = System.getenv("EMAIL_PASS");

        if (username == null || password == null) {
            throw new IllegalStateException("Environment variables EMAIL_USER and EMAIL_PASS must be set.");
        }

        props = new Properties();
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
