package com.example;
import java.net.URL;
import java.net.MalformedURLException;
public class UrlParser {
    public static void main(String[] args) {
        String urlString = args.length > 0 ? args[0] : "https://example.com:8080/path/to/resource?user=test#section";
        try {
            URL url = new URL(urlString);
            System.out.println("Protocol: " + url.getProtocol());
            System.out.println("Host: " + url.getHost());
            int port = url.getPort();
            if (port != -1) {
                System.out.println("Port: " + port);}
            System.out.println("Path: " + url.getPath());
            String query = url.getQuery();
            if (query != null) {
                System.out.println("Query: " + query);}
            String fragment = url.getRef();
            if (fragment != null) {
                System.out.println("Fragment: " + fragment);}
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL: " + urlString);}}}