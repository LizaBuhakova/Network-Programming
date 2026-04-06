package ua.edu.chmnu.net_dev.c4.tcp.echo.client;
import java.io.*;
import java.net.*;
import java.util.Random;
public class MultiThreadClient {
    private static final int PORT = 6710;
    private static final String HOST = "localhost";
    private static final int NUM_CLIENTS = 1000;
    public static void main(String[] args) {
        Thread[] clients = new Thread[NUM_CLIENTS];
        for (int i = 0; i < NUM_CLIENTS; i++) {
            clients[i] = new Thread(new ClientSession(i));
            clients[i].start();}
        for (Thread t : clients) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();}}}
    private static class ClientSession implements Runnable {
        private int id;
        public ClientSession(int id) {
            this.id = id;}
        public void run() {
            try (Socket socket = new Socket(HOST, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String promptNick = in.readLine();
                String nick = "Client" + id;
                out.println(nick);
                String promptData = in.readLine();
                String randomString = generateRandomString(10);
                long startTime = System.nanoTime();
                out.println(randomString);
                String response = in.readLine();
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                System.out.println("Client " + id + ": Sent " + randomString + ", Received " + response + ", Duration: " + duration + " ns");
                out.println("Q");
                in.readLine();
            } catch (IOException e) {
                e.printStackTrace();}}
        private String generateRandomString(int length) {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));}
            return sb.toString();}}}
