package com.mystore.utility;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;

public class EmailReport {

    // Gmail credentials
    private static final String FROM_EMAIL = "priti.kasar@magnetoitsolutions.com";
    private static final String PASSWORD = "vmtlarolrpizbmwg";

    // Comma-separated recipient list
    private static final String TO_EMAILS = "kaspritiautomation@gmail.com";

    public static void sendReport(String reportPath) {
        // Set SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create session with authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));

            // Safely parse multiple recipients
            InternetAddress[] recipientAddresses = InternetAddress.parse(TO_EMAILS);
            for (InternetAddress address : recipientAddresses) {
                address.validate(); // optional: ensures each email is valid
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);

            message.setSubject("Automation Test Report");

            // Create the message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Please find the attached test report.");

            // Create multipart and attach body and file
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart attachPart = new MimeBodyPart();
            attachPart.attachFile(new File(reportPath));
            multipart.addBodyPart(attachPart);

            message.setContent(multipart);

            // Send the email
            Transport.send(message);
            System.out.println("📧 Report emailed successfully!");

        } catch (Exception e) {
            System.err.println("❌ Failed to send email:");
            e.printStackTrace();
        }
    }
}