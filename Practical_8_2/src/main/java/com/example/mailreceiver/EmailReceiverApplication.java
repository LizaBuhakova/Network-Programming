package com.example.mailreceiver;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import com.sun.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class EmailReceiverApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailReceiverApplication.class);
    private static final String CONFIG_FILE = "email.properties";
    private final EmailReceiverConfig config;
    private final Set<String> seenFallbackKeys = new HashSet<>();
    public EmailReceiverApplication(EmailReceiverConfig config) {
        this.config = config;}
    public static void main(String[] args) {
        try {
            EmailReceiverConfig config = loadConfig(CONFIG_FILE);
            EmailReceiverApplication app = new EmailReceiverApplication(config);
            app.start();
        } catch (Exception e) {
            LOGGER.error("Application failed to start: {}", e.getMessage(), e);}}
    private static EmailReceiverConfig loadConfig(String configPath) throws IOException {
        Path path = Paths.get(configPath);
        if (!Files.exists(path)) {
            throw new IOException("Configuration file not found: " + configPath +
                    ". Copy email.properties.example to email.properties and fill it in.");}
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configPath)) {
            properties.load(input);}
        return EmailReceiverConfig.fromProperties(properties);}
    public void start() {
        LOGGER.info("Starting e-mail receiver. Poll interval: {} seconds", config.pollIntervalSeconds());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::safePollInbox, 0, config.pollIntervalSeconds(), TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutdown requested. Stopping receiver...");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executor.shutdownNow();}}));}
    private void safePollInbox() {
        try {
            pollInbox();
        } catch (Exception e) {
            LOGGER.error("Polling error: {}", e.getMessage(), e);}}
    private void pollInbox() throws MessagingException {
        Properties sessionProperties = new Properties();
        sessionProperties.put("mail.store.protocol", config.protocol());
        sessionProperties.put("mail." + config.protocol() + ".host", config.host());
        sessionProperties.put("mail." + config.protocol() + ".port", String.valueOf(config.port()));
        sessionProperties.put("mail." + config.protocol() + ".ssl.enable", String.valueOf(config.sslEnabled()));
        sessionProperties.put("mail." + config.protocol() + ".starttls.enable", String.valueOf(config.startTlsEnabled()));
        Session session = Session.getInstance(sessionProperties);
        try (Store store = session.getStore(config.protocol())) {
            store.connect(config.host(), config.port(), config.username(), config.password());
            Folder folder = store.getFolder(config.folderName());
            folder.open(Folder.READ_ONLY);
            try {
                Message[] messages = folder.getMessages();
                for (Message message : messages) {
                    if (isNewMessage(folder, message)) {
                        logMessage(message);}}
            } finally {
                folder.close(false);}}}
    private boolean isNewMessage(Folder folder, Message message) throws MessagingException {
        if (folder instanceof IMAPFolder) {
            IMAPFolder imapFolder = (IMAPFolder) folder;
            long uid = imapFolder.getUID(message);
            String uidKey = "UID:" + uid;
            return seenFallbackKeys.add(uidKey);}
        String fallbackKey = "MSG:" + message.getMessageNumber() +
                "|DATE:" + (message.getSentDate() != null ? message.getSentDate().getTime() : 0L) +
                "|SUB:" + (message.getSubject() != null ? message.getSubject() : "");
        return seenFallbackKeys.add(fallbackKey);}
    private void logMessage(Message message) throws MessagingException {
        String sender = "Unknown";
        if (message.getFrom() != null && message.getFrom().length > 0 && message.getFrom()[0] instanceof InternetAddress) {
            InternetAddress from = (InternetAddress) message.getFrom()[0];
            sender = from.getAddress();}
        String subject = message.getSubject() != null ? message.getSubject() : "(no subject)";
        LOGGER.info("New e-mail -> From: {} | Subject: {}", sender, subject);}}
