package com.example.mailreceiver;
import java.util.Properties;
public class EmailReceiverConfig {
    private final String protocol;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String folderName;
    private final boolean sslEnabled;
    private final boolean startTlsEnabled;
    private final int pollIntervalSeconds;
    public EmailReceiverConfig(String protocol,
                               String host,
                               int port,
                               String username,
                               String password,
                               String folderName,
                               boolean sslEnabled,
                               boolean startTlsEnabled,
                               int pollIntervalSeconds) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.folderName = folderName;
        this.sslEnabled = sslEnabled;
        this.startTlsEnabled = startTlsEnabled;
        this.pollIntervalSeconds = pollIntervalSeconds;}
    public static EmailReceiverConfig fromProperties(Properties properties) {
        String protocol = required(properties, "mail.protocol");
        String host = required(properties, "mail.host");
        int port = Integer.parseInt(required(properties, "mail.port"));
        String username = required(properties, "mail.username");
        String password = required(properties, "mail.password");
        String folderName = properties.getProperty("mail.folder", "INBOX");
        boolean sslEnabled = Boolean.parseBoolean(properties.getProperty("mail.ssl.enable", "true"));
        boolean startTlsEnabled = Boolean.parseBoolean(properties.getProperty("mail.starttls.enable", "false"));
        int pollIntervalSeconds = Integer.parseInt(properties.getProperty("mail.poll.interval.seconds", "30"));
        return new EmailReceiverConfig(
                protocol,
                host,
                port,
                username,
                password,
                folderName,
                sslEnabled,
                startTlsEnabled,
                pollIntervalSeconds);}
    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required property: " + key);}
        return value.trim();}
    public String protocol() {
        return protocol;}
    public String host() {
        return host;}
    public int port() {
        return port;}
    public String username() {
        return username;}
    public String password() {
        return password;}
    public String folderName() {
        return folderName;}
    public boolean sslEnabled() {
        return sslEnabled;}
    public boolean startTlsEnabled() {
        return startTlsEnabled;}
    public int pollIntervalSeconds() {
        return pollIntervalSeconds;}}
