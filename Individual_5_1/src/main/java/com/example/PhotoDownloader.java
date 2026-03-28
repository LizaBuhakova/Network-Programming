package com.example;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class PhotoDownloader {
    public static void download(String urlString, String outputFile) throws IOException {
        long startTime = System.currentTimeMillis();
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Downloaded to " + outputFile + " in " + (endTime - startTime) + " ms");
    }
}