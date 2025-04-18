package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "diopl46800@gmail.com";
    private static final String EMAIL_PASSWORD = "rxlzbqkprlxoovei";

    public static boolean envoyerEmail(String emailTo, String sujet, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(EMAIL_FROM));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
            emailMessage.setSubject(sujet);
            emailMessage.setText(message);

            Transport.send(emailMessage);
            System.out.println("✅ Email envoyé avec succès à " + emailTo);
            return true;
        } catch (MessagingException e) {
            System.err.println("❌ Erreur d'envoi d'email : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
