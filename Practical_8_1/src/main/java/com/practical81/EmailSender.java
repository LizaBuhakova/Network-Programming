package com.practical81;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Date;
public class EmailSender {
    public void sendHtmlEmailWithAttachments(SmtpConfig smtpConfig, EmailMessageConfig messageConfig) throws MessagingException {
        Authenticator authenticator = null;
        if (smtpConfig.requiresAuthentication()) {
            authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpConfig.getUsername(), smtpConfig.getPassword());}};}
        Session session = Session.getInstance(smtpConfig.toMailProperties(), authenticator);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(messageConfig.getFrom()));
        message.setSentDate(new Date());
        for (String recipient : messageConfig.getRecipients()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));}
        message.setSubject(messageConfig.getSubject(), "UTF-8");
        MimeMultipart multipart = new MimeMultipart("mixed");
        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(messageConfig.getHtmlContent(), "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlBodyPart);
        for (String attachmentPath : messageConfig.getAttachmentPaths()) {
            File file = new File(attachmentPath);
            if (!file.exists() || !file.isFile()) {
                throw new MessagingException("Attachment not found: " + attachmentPath);}
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(file.getName());
            multipart.addBodyPart(attachmentBodyPart);}
        message.setContent(multipart);
        Transport.send(message);}}