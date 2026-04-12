package ua.network.udp.client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
public class UdpClientApp {
    private static final int DEFAULT_LOCAL_PORT = 9101;
    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 9000;
    public static void main(String[] args) {
        String clientId = args.length > 0 ? args[0] : "client1";
        int localPort = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_LOCAL_PORT;
        String serverHost = args.length > 2 ? args[2] : DEFAULT_SERVER_HOST;
        int serverPort = args.length > 3 ? Integer.parseInt(args[3]) : DEFAULT_SERVER_PORT;
        try (DatagramSocket socket = new DatagramSocket(localPort)) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);
            System.out.printf("UDP client '%s' started on port %d%n", clientId, localPort);
            System.out.printf("Server: %s:%d%n", serverHost, serverPort);
            printHelp();
            Thread receiverThread = new Thread(() -> receiveLoop(socket), "client-receiver");
            receiverThread.setDaemon(true);
            receiverThread.start();
            try (java.util.Scanner scanner = new java.util.Scanner(System.in, "UTF-8")) {
                while (true) {
                    System.out.print("client> ");
                    if (!scanner.hasNextLine()) {
                        break;}
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;}
                    if ("exit".equalsIgnoreCase(line)) {
                        break;}
                    if ("help".equalsIgnoreCase(line)) {
                        printHelp();
                        continue;}
                    String payload = clientId + ": " + line;
                    sendToServer(socket, serverAddress, serverPort, payload);}}
        } catch (SocketException e) {
            System.err.println("Failed to open local client port: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());}
        System.out.println("Client stopped.");}
    private static void receiveLoop(DatagramSocket socket) {
        byte[] buffer = new byte[2048];
        while (!socket.isClosed()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                String text = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.printf("%n[Message from %s:%d] %s%nclient> ",
                        packet.getAddress().getHostAddress(),
                        packet.getPort(),
                        text);
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("Receive error: " + e.getMessage());}}}}
    private static void sendToServer(DatagramSocket socket,
                                     InetAddress serverAddress,
                                     int serverPort,
                                     String payload) throws IOException {
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
        socket.send(packet);
        System.out.println("[Sent to server] " + payload);}
    private static void printHelp() {
        System.out.println("Client commands:");
        System.out.println("  <any text>         - send message to server");
        System.out.println("  help               - show help");
        System.out.println("  exit               - stop client");}}