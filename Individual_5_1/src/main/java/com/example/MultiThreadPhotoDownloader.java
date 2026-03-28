package com.example;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadPhotoDownloader {
    public static void downloadAll(String[] urls, String outputDir) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            String fileName = "multi_photo" + (i + 1) + ".jpg";
            String outputFile = outputDir + File.separator + fileName;
            executor.submit(() -> {
                try {
                    download(url, outputFile);
                    System.out.println("Downloaded " + url + " to " + outputFile);
                } catch (IOException e) {
                    System.err.println("Error downloading " + url + ": " + e.getMessage());
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();
        System.out.println("All downloads completed in " + (endTime - startTime) + " ms");
    }

    private static void download(String urlString, String outputFile) throws IOException {
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
    }
}