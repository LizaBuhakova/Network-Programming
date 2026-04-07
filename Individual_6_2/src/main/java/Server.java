import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clients = new HashMap<>();
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущено на порту " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();}
        } catch (IOException e) {
            e.printStackTrace();}}
    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String clientName = in.readLine();
            synchronized (clients) {
                clients.put(clientName, out);}
            System.out.println("Клієнт " + clientName + " підключився");
            String message;
            while ((message = in.readLine()) != null) {
                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String recipient = parts[0];
                    String msg = parts[1];
                    synchronized (clients) {
                        PrintWriter recipientOut = clients.get(recipient);
                        if (recipientOut != null) {
                            recipientOut.println(clientName + ": " + msg);
                        } else {
                            out.println("Одержувач " + recipient + " не знайдено");}}}}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();}}}}                