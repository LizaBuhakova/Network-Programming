package ua.network.udp.server;
import ua.network.udp.common.ClientInfo;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
public class UdpServerApp {
    private static final int DEFAULT_SERVER_PORT = 9000;
    private static final String DEFAULT_CLIENTS_FILE = "clients.txt";
    public static void main(String[] args) {
        int serverPort = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_SERVER_PORT;
        Path clientsFile = Paths.get(args.length > 1 ? args[1] : DEFAULT_CLIENTS_FILE);
        ClientRegistry registry = new ClientRegistry(clientsFile);
        try {
            registry.load();
        } catch (IOException e) {
            System.err.println("Failed to load clients file: " + e.getMessage());
            return;}
        ExecutorService sendPool = Executors.newFixedThreadPool(8);
        try (DatagramSocket serverSocket = new DatagramSocket(serverPort)) {
            System.out.println("UDP server started on port " + serverPort);
            System.out.println("Clients file: " + clientsFile.toAbsolutePath());
            printHelp();
            Thread receiverThread = new Thread(() -> receiveLoop(serverSocket), "server-receiver");
            receiverThread.setDaemon(true);
            receiverThread.start();
            Thread consoleThread = new Thread(
                    () -> handleConsoleInput(serverSocket, registry, sendPool),
                    "server-console");
            consoleThread.start();
            consoleThread.join();
        } catch (SocketException e) {
            System.err.println("Failed to open server port: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            sendPool.shutdownNow();
            System.out.println("Server stopped.");}}
    private static void receiveLoop(DatagramSocket socket) {
        byte[] buffer = new byte[2048];
        while (!socket.isClosed()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                String text = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.printf("[Received from %s:%d] %s%n",
                        packet.getAddress().getHostAddress(),
                        packet.getPort(),
                        text);
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("Packet receive error: " + e.getMessage());}}}}
    private static void handleConsoleInput(DatagramSocket socket, ClientRegistry registry, ExecutorService sendPool) {
        try (java.util.Scanner scanner = new java.util.Scanner(System.in, "UTF-8")) {
            while (true) {
                System.out.print("server> ");
                if (!scanner.hasNextLine()) {
                    return;}
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;}
                if ("exit".equalsIgnoreCase(line)) {
                    socket.close();
                    return;}
                if ("help".equalsIgnoreCase(line)) {
                    printHelp();
                    continue;}
                if ("list".equalsIgnoreCase(line)) {
                    registry.getAll().forEach(System.out::println);
                    continue;}
                if ("reload".equalsIgnoreCase(line)) {
                    registry.load();
                    System.out.println("Clients list reloaded.");
                    continue;}
                if (line.toLowerCase().startsWith("send ")) {
                    processSendCommand(line.substring(5), socket, registry, sendPool);
                    continue;}
                System.out.println("Unknown command. Type 'help'.");}
        } catch (Exception e) {
            System.err.println("Server console error: " + e.getMessage());
            socket.close();}}
    private static void processSendCommand(String payload,
                                           DatagramSocket socket,
                                           ClientRegistry registry,
                                           ExecutorService sendPool) {
        String[] parts = payload.split("\\s+", 2);
        if (parts.length < 2) {
            System.out.println("Usage: send <all|id1,id2> <message>");
            return;}
        String selector = parts[0].trim();
        String message = parts[1].trim();
        List<ClientInfo> targets;
        try {
            if ("all".equalsIgnoreCase(selector)) {
                targets = registry.getAll().stream().collect(Collectors.toList());
            } else {
                List<String> ids = Arrays.stream(selector.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                targets = registry.getByIds(ids);}
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;}
        if (targets.isEmpty()) {
            System.out.println("No recipients selected.");
            return;}
        for (ClientInfo target : targets) {
            sendPool.submit(() -> sendMessage(socket, target, message));}}
    private static void sendMessage(DatagramSocket socket, ClientInfo client, String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, client.getAddress(), client.getPort());
        try {
            socket.send(packet);
            System.out.printf("[Sent -> %s] %s%n", client.getId(), message);
        } catch (IOException e) {
            System.err.printf("Failed to send to %s: %s%n", client.getId(), e.getMessage());}}
    private static void printHelp() {
        System.out.println("Server commands:");
        System.out.println("  list                          - show clients from file");
        System.out.println("  reload                        - reload clients.txt");
        System.out.println("  send all <message>            - send to all clients");
        System.out.println("  send id1,id2 <message>        - send to selected clients");
        System.out.println("  help                          - show help");
        System.out.println("  exit                          - stop server");}}