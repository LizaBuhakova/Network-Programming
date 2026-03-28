package com.example;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -cp target/classes com.example.Main <mode> <urls...>");
            System.out.println("Mode: single <url> <outputFile>");
            System.out.println("Mode: multi <outputDir> <url1> <url2> ...");
            return;
        }

        String mode = args[0];

        if ("single".equals(mode) && args.length == 3) {
            String url = args[1];
            String outputFile = args[2];
            try {
                PhotoDownloader.download(url, outputFile);
                System.out.println("Downloaded to " + outputFile);
            } catch (IOException e) {
                System.err.println("Error downloading: " + e.getMessage());
            }
        } else if ("multi".equals(mode) && args.length >= 3) {
            String outputDir = args[1];
            String[] urls = Arrays.copyOfRange(args, 2, args.length);
            try {
                MultiThreadPhotoDownloader.downloadAll(urls, outputDir);
                System.out.println("All downloads completed.");
            } catch (IOException | InterruptedException e) {
                System.err.println("Error downloading: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid arguments.");
        }
    }
}