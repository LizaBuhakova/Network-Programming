package com.example.downloader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
public final class FileUtils {
    private FileUtils() {}
    public static String safeFilename(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            String path = u.getPath();
            if (path == null || path.isEmpty() || path.endsWith("/")) {
                return "index.html";}
            String file = path.substring(path.lastIndexOf('/') + 1);
            if (file.isEmpty()) {
                return "index.html";}
            return file.replaceAll("[^A-Za-z0-9._-]", "_");
        } catch (Exception ex) {
            return "downloaded_file";}}
    public static boolean fileExistsWithSize(Path path, long expectedSize) {
        try {
            return Files.exists(path) && Files.size(path) == expectedSize;
        } catch (IOException e) {
            return false;}}
    public static String ensureExtension(String filename, String contentType) {
        if (filename.contains(".")) {
            return filename;}
        if (contentType == null) {
            return filename;}
        String type = contentType.split(";")[0].trim().toLowerCase();
        String ext = "";
        if ("image/jpeg".equals(type)) {
            ext = ".jpg";
        } else if ("image/png".equals(type)) {
            ext = ".png";
        } else if ("image/gif".equals(type)) {
            ext = ".gif";
        } else if ("image/webp".equals(type)) {
            ext = ".webp";
        } else if ("image/bmp".equals(type)) {
            ext = ".bmp";
        } else if ("image/svg+xml".equals(type)) {
            ext = ".svg";
        } else if ("application/pdf".equals(type)) {
            ext = ".pdf";
        } else if ("text/plain".equals(type)) {
            ext = ".txt";
        } else if ("text/html".equals(type)) {
            ext = ".html";}
        return ext.isEmpty() ? filename : filename + ext;}
    public static String sha256(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            try (DigestInputStream dis = new DigestInputStream(fis, md)) {
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1) {}}
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));}
            return sb.toString();
        } catch (Exception e) {
            return null;}}}
