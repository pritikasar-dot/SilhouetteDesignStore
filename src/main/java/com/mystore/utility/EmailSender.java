package com.mystore.utility;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;

public class EmailSender {

    public static boolean sendEmailWithAttachments(String host, String port, String user, String pass,
                                                   String toAddress, String subject, String htmlMessage,
                                                   List<String> attachments) {
        boolean result = false;

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            msg.setSubject(subject);

            // Body part
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(htmlMessage, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            // Attach files
            if (attachments != null) {
                for (String filePath : attachments) {
                    if (filePath != null && new File(filePath).exists()) {
                        MimeBodyPart attach = new MimeBodyPart();
                        attach.attachFile(filePath);
                        multipart.addBodyPart(attach);
                    }
                }
            }

            msg.setContent(multipart);
            Transport.send(msg);
            result = true;
            System.out.println("✅ Email sent with attachments.");

        } catch (Exception e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}