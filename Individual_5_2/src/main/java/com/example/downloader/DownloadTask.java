package com.example.downloader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
public class DownloadTask implements Runnable {
    public interface StatusCallback {
        void onStatus(int index, String status);
        void onProgress(int index, String progress);
        void onSpeed(int index, String speed);}
    private final String url;
    private final String filename;
    private final StatusCallback callback;
    private final Path initialTargetFile;
    private int index;
    private AtomicBoolean cancelled;
    public DownloadTask(String url, String filename, StatusCallback callback) {
        this.url = url;
        this.filename = filename;
        this.callback = callback;
        this.initialTargetFile = Paths.get(filename);}
    void setIndex(int index) {
        this.index = index;}
    void setCancelledFlag(AtomicBoolean cancelled) {
        this.cancelled = cancelled;}
    @Override
    public void run() {
        try {
            updateStatus("Checking");
            URL source = new URL(url);
            HttpURLConnection headConn = (HttpURLConnection) source.openConnection();
            headConn.setRequestMethod("HEAD");
            headConn.setConnectTimeout(15_000);
            headConn.setReadTimeout(15_000);
            headConn.connect();
            int code = headConn.getResponseCode();
            if (code >= 400) {
                updateStatus("HTTP " + code);
                return;}
            long remoteSize = headConn.getContentLengthLong();
            String eTag = headConn.getHeaderField("ETag");
            String lastModified = headConn.getHeaderField("Last-Modified");
            String contentType = headConn.getHeaderField("Content-Type");
            String finalFileName = FileUtils.ensureExtension(filename, contentType);
            Path targetFile = Paths.get(finalFileName);
            if (isAlreadyDownloaded(source, targetFile, remoteSize, contentType)) {
                updateStatus("Already in sync");
                updateProgress("100%");
                updateSpeed("0 KB/s");
                return;}
            boolean supportsRange = supportsRange(source);
            long offset = 0;
            if (Files.exists(targetFile)) {
                offset = Files.size(targetFile);
                if (remoteSize >= 0 && offset > remoteSize) {
                    Files.delete(targetFile);
                    offset = 0;}}
            if (offset > 0 && !supportsRange) {
                Files.deleteIfExists(targetFile);
                offset = 0;}
            HttpURLConnection conn = (HttpURLConnection) source.openConnection();
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(15_000);
            if (offset > 0) {
                conn.setRequestProperty("Range", "bytes=" + offset + "-");}
            try (InputStream in = conn.getInputStream();
                 OutputStream out = new FileOutputStream(targetFile.toFile(), true)) {
                updateStatus("Downloading");
                byte[] buffer = new byte[8192];
                int read;
                long downloaded = offset;
                long lastBytes = downloaded;
                long lastTime = System.nanoTime();
                while ((read = in.read(buffer)) != -1) {
                    if (cancelled != null && cancelled.get()) {
                        updateStatus("Stopped");
                        return;}
                    out.write(buffer, 0, read);
                    downloaded += read;
                    if (remoteSize > 0) {
                        updateProgress(String.format("%.2f%%", downloaded * 100.0 / remoteSize));
                    } else {
                        updateProgress(downloaded + " bytes");}
                    long now = System.nanoTime();
                    if (now - lastTime >= 1_000_000_000L) {
                        double rate = (downloaded - lastBytes) / 1024.0 / ((now - lastTime) / 1_000_000_000.0);
                        updateSpeed(String.format("%.2f KB/s", rate));
                        lastTime = now;
                        lastBytes = downloaded;}}}
            updateProgress("100%");
            updateSpeed("0 KB/s");
            updateStatus("Completed");
        } catch (Exception e) {
            updateStatus("Error: " + e.getClass().getSimpleName());
            updateSpeed("0 KB/s");}}
    private boolean supportsRange(URL source) {
        try {
            HttpURLConnection conn = (HttpURLConnection) source.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(15_000);
            conn.connect();
            String accept = conn.getHeaderField("Accept-Ranges");
            return accept != null && accept.equalsIgnoreCase("bytes");
        } catch (Exception e) {
            return false;}}
    private boolean isAlreadyDownloaded(URL source, Path targetFile, long remoteSize, String contentType) {
        try {
            if (!Files.exists(targetFile)) return false;
            if (remoteSize >= 0 && Files.size(targetFile) != remoteSize) return false;
            if (remoteSize >= 0) {
                String localHash = FileUtils.sha256(targetFile.toFile());
                String remoteHash = hashRemote(source);
                return localHash != null && remoteHash != null && localHash.equals(remoteHash);}
            return true;
        } catch (Exception e) {
            return false;}}
    private String hashRemote(URL source) {
        try {
            HttpURLConnection conn = (HttpURLConnection) source.openConnection();
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(15_000);
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() >= 400) return null;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            try (InputStream in = conn.getInputStream(); DigestInputStream dis = new DigestInputStream(in, md)) {
                byte[] buf = new byte[8192];
                while (dis.read(buf) != -1) {
                    if (cancelled != null && cancelled.get()) {
                        return null;}}}
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));}
            return sb.toString();
        } catch (Exception e) {
            return null;}}
    private void updateStatus(String status) {
        callback.onStatus(index, status);}
    private void updateProgress(String progress) {
        callback.onProgress(index, progress);}
    private void updateSpeed(String speed) {
        callback.onSpeed(index, speed);}}
