import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private JFrame frame;
    private JTextArea logArea;
    private DefaultListModel<String> clientListModel;
    private JTextField portField;
    private JButton startButton;
    private JLabel statusLabel;

    private ServerSocket serverSocket;
    private Thread acceptThread;
    private final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    private final AtomicInteger nextClientId = new AtomicInteger(1);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Server::new);
    }

    public Server() {
        frame = new JFrame("TCP Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345", 8);
        topPanel.add(portField);
        startButton = new JButton("Start Server");
        startButton.addActionListener(this::toggleServer);
        topPanel.add(startButton);
        statusLabel = new JLabel("Stopped");
        topPanel.add(statusLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Server Log"));

        clientListModel = new DefaultListModel<>();
        JList<String> clientList = new JList<>(clientListModel);
        JScrollPane clientScroll = new JScrollPane(clientList);
        clientScroll.setBorder(BorderFactory.createTitledBorder("Connected Clients"));
        clientScroll.setPreferredSize(new Dimension(180, 0));

        frame.add(clientScroll, BorderLayout.EAST);
        frame.add(logScroll, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void toggleServer(ActionEvent event) {
        if (serverSocket == null) {
            startServer();
        } else {
            stopServer();
        }
    }

    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            serverSocket = new ServerSocket(port);
            appendLog("Server started on port " + port);
            statusLabel.setText("Running");
            startButton.setText("Stop Server");
            acceptThread = new Thread(this::acceptLoop);
            acceptThread.start();
        } catch (IOException | NumberFormatException e) {
            appendLog("Failed to start server: " + e.getMessage());
        }
    }

    private void stopServer() {
        appendLog("Stopping server...");
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
        serverSocket = null;
        if (acceptThread != null) {
            acceptThread.interrupt();
        }
        clients.values().forEach(ClientHandler::close);
        clients.clear();
        updateClientList();
        statusLabel.setText("Stopped");
        startButton.setText("Start Server");
        appendLog("Server stopped");
    }

    private void acceptLoop() {
        while (serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                int clientId = nextClientId.getAndIncrement();
                ClientHandler handler = new ClientHandler(clientId, clientSocket);
                clients.put(clientId, handler);
                handler.start();
                appendLog("Client connected: id=" + clientId + " address=" + clientSocket.getRemoteSocketAddress());
                broadcastClientList();
            } catch (IOException e) {
                if (serverSocket == null || serverSocket.isClosed()) {
                    break;
                }
                appendLog("Accept error: " + e.getMessage());
            }
        }
    }

    private void appendLog(String text) {
        SwingUtilities.invokeLater(() -> logArea.append(text + "\n"));
    }

    private void broadcastClientList() {
        StringBuilder builder = new StringBuilder();
        builder.append("CLIENTS:");
        boolean first = true;
        for (ClientHandler client : clients.values()) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            builder.append(client.id).append("=").append(client.name);
        }
        String message = builder.toString();
        clients.values().forEach(c -> c.sendLine(message));
        updateClientList();
    }

    private void updateClientList() {
        SwingUtilities.invokeLater(() -> {
            clientListModel.clear();
            for (ClientHandler client : clients.values()) {
                clientListModel.addElement(client.id + ": " + client.name);
            }
        });
    }

    private class ClientHandler extends Thread {
        private final int id;
        private final Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String name;
        private volatile boolean active = true;

        ClientHandler(int id, Socket socket) {
            this.id = id;
            this.socket = socket;
            this.name = "Client" + id;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                sendLine("ID:" + id);
                sendLine("CLIENTS:");

                String line;
                while (active && (line = reader.readLine()) != null) {
                    if (line.startsWith("NAME:")) {
                        name = line.substring(5).trim();
                        appendLog("Client " + id + " set name: " + name);
                        broadcastClientList();
                    } else if (line.startsWith("SEND:")) {
                        String[] parts = line.split(":", 3);
                        if (parts.length < 3) {
                            sendLine("ERROR:Invalid send format");
                            continue;
                        }
                        int targetId;
                        try {
                            targetId = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            sendLine("ERROR:Target id must be a number");
                            continue;
                        }
                        String message = parts[2];
                        ClientHandler target = clients.get(targetId);
                        if (target == null) {
                            sendLine("ERROR:Client " + targetId + " not connected");
                        } else {
                            target.sendLine("FROM:" + id + ":" + name + ":" + message);
                            appendLog("Message from " + id + " to " + targetId + ": " + message);
                        }
                    } else {
                        sendLine("ERROR:Unknown command");
                    }
                }
            } catch (IOException e) {
                appendLog("Client " + id + " connection error: " + e.getMessage());
            } finally {
                close();
            }
        }

        void sendLine(String line) {
            if (writer != null) {
                writer.println(line);
            }
        }

        void close() {
            active = false;
            clients.remove(id);
            updateClientList();
            broadcastClientList();
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
            appendLog("Client disconnected: id=" + id + " name=" + name);
        }
    }
}
