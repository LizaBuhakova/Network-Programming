package com.practical81;
import java.util.Properties;
public class SmtpConfig {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean auth;
    private final boolean startTls;
    private final boolean ssl;
    public SmtpConfig(String host,
                      int port,
                      String username,
                      String password,
                      boolean auth,
                      boolean startTls,
                      boolean ssl) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.auth = auth;
        this.startTls = startTls;
        this.ssl = ssl;}
    public String getUsername() {
        return username;}
    public String getPassword() {
        return password;}
    public boolean requiresAuthentication() {
        return auth;}
    public Properties toMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
        properties.put("mail.smtp.auth", String.valueOf(auth));
        properties.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        properties.put("mail.smtp.ssl.enable", String.valueOf(ssl));
        return properties;}}
