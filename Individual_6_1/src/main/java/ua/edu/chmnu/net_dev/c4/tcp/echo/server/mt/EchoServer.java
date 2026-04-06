package ua.edu.chmnu.net_dev.c4.tcp.echo.server.mt;
import java.io.*;
import java.net.*;
public class EchoServer {
    private static final int PORT = 6710;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();}
        } catch (IOException e) {
            e.printStackTrace();}}
    private static class ClientHandler extends Thread {
        private Socket socket;
        public ClientHandler(Socket socket) {
            this.socket = socket;}
        public void run() {
            try (Socket socket = this.socket) {
                try (BufferedReader ir = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    System.out.println("Establishing connection from: " + socket.getRemoteSocketAddress());
                    String promptNick = "Enter your nick:";
                    String promptData = "Enter message (Q to quit):";
                    writer.println(promptNick);
                    String nick = ir.readLine();
                    System.out.println("Client nick: " + nick);
                    String inPrefix = "[" + nick + "] < ";
                    String outPrefix = "[" + nick + "] > ";
                    while (!socket.isClosed()) {
                        try {
                            System.out.println("Waiting text from: " + nick);
                            writer.println(promptData);
                            String inLine = ir.readLine();
                            if (inLine == null || inLine.trim().isEmpty()) {
                                break;
                            }
                            System.out.println(inPrefix + inLine);
                            String outLine = inverse(inLine);
                            writer.println(outLine);
                            System.out.println(outPrefix + outLine);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;}}}
            } catch (IOException e) {
                e.printStackTrace();}}
        private String inverse(String inLine) {
            return new StringBuilder(inLine).reverse().toString();}}}