/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package takealot.dot.com.service;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mmetsa
 */
@Service
public class EmailService {

    public void sendEmail(String subject, String emailBody, String userEmail) throws MessagingException {
        System.out.println("Attempting to send an email");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("lesediprincessmoloto@gmail.com", "Lesedi@939!");

            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("fistos090@gmail.com"));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(userEmail));
        message.setSubject(subject);
        message.setContent(emailBody, "text/html");

        Transport.send(message);

    }

}
