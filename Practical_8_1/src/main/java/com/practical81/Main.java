package com.practical81;
import jakarta.mail.MessagingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
public class Main {
    public static void main(String[] args) {
        try {
            SmtpConfig smtpConfig = new SmtpConfig(
                    getEnvOrThrow("SMTP_HOST"),
                    Integer.parseInt(getEnvOrDefault("SMTP_PORT", "587")),
                    getEnvOrDefault("SMTP_USERNAME", ""),
                    getEnvOrDefault("SMTP_PASSWORD", ""),
                    Boolean.parseBoolean(getEnvOrDefault("SMTP_AUTH", "true")),
                    Boolean.parseBoolean(getEnvOrDefault("SMTP_STARTTLS", "true")),
                    Boolean.parseBoolean(getEnvOrDefault("SMTP_SSL", "false")));
            List<String> recipients = splitCsv(getEnvOrThrow("MAIL_TO"));
            List<String> attachments = splitCsv(getEnvOrDefault("MAIL_ATTACHMENTS", ""));
            EmailMessageConfig messageConfig = new EmailMessageConfig(
                    getEnvOrThrow("MAIL_FROM"),
                    recipients,
                    getEnvOrDefault("MAIL_SUBJECT", "Practical #8.1 - HTML email"),
                    getEnvOrDefault("MAIL_HTML", "<h1>Hello from Java + Maven</h1><p>HTML e-mail with attachments.</p>"),
                    attachments);
            new EmailSender().sendHtmlEmailWithAttachments(smtpConfig, messageConfig);
            System.out.println("E-mail sent successfully.");
        } catch (MessagingException e) {
            System.err.println("Failed to send e-mail: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Configuration error: " + e.getMessage());}}
    private static List<String> splitCsv(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptyList();}
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());}
    private static String getEnvOrThrow(String key) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required environment variable: " + key);}
        return value;}
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;}}