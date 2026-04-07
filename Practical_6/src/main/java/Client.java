import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField serverField;
    private JTextField portField;
    private JTextField nameField;
    private JButton connectButton;
    private JComboBox<String> targetCombo;
    private JTextField messageField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread listenerThread;
    private final Map<Integer, String> clientNames = new HashMap<>();
    private int clientId = -1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    public Client() {
        frame = new JFrame("TCP Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.add(new JLabel("Server:"));
        serverField = new JTextField("localhost", 10);
        connectionPanel.add(serverField);
        connectionPanel.add(new JLabel("Port:"));
        portField = new JTextField("12345", 5);
        connectionPanel.add(portField);
        connectionPanel.add(new JLabel("Name:"));
        nameField = new JTextField("User", 10);
        connectionPanel.add(nameField);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this::toggleConnection);
        connectionPanel.add(connectButton);

        frame.add(connectionPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createTitledBorder("Chat"));
        frame.add(chatScroll, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        targetCombo = new JComboBox<>();
        targetCombo.addItem("Select target");
        targetCombo.setPreferredSize(new Dimension(200, 25));
        sendPanel.add(targetCombo);
        messageField = new JTextField(25);
        sendPanel.add(messageField);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this::sendMessage);
        sendButton.setEnabled(false);
        sendPanel.add(sendButton);

        frame.add(sendPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void toggleConnection(ActionEvent event) {
        if (socket == null || socket.isClosed()) {
            connect();
        } else {
            disconnect();
        }
    }

    private void connect() {
        try {
            String server = serverField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            socket = new Socket(server, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("NAME:" + nameField.getText().trim());
            listenerThread = new Thread(this::listenLoop);
            listenerThread.start();
            appendChat("Connected to server at " + server + ":" + port);
            connectButton.setText("Disconnect");
            sendButton.setEnabled(true);
        } catch (IOException | NumberFormatException e) {
            appendChat("Connection failed: " + e.getMessage());
        }
    }

    private void disconnect() {
        appendChat("Disconnecting...");
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
        socket = null;
        clientId = -1;
        clientNames.clear();
        updateTargetList();
        connectButton.setText("Connect");
        sendButton.setEnabled(false);
        appendChat("Disconnected");
    }

    private void listenLoop() {
        try {
            String line;
            while (socket != null && (line = reader.readLine()) != null) {
                if (line.startsWith("ID:")) {
                    clientId = Integer.parseInt(line.substring(3).trim());
                    appendChat("Assigned client ID: " + clientId);
                } else if (line.startsWith("CLIENTS:")) {
                    updateClientList(line.substring(8));
                } else if (line.startsWith("FROM:")) {
                    String[] parts = line.split(":", 4);
                    if (parts.length < 4) {
                        appendChat("Invalid message format from server");
                    } else {
                        int fromId = Integer.parseInt(parts[1]);
                        String fromName = parts[2];
                        String message = parts[3];
                        appendChat("Message from " + fromName + " (" + fromId + "): " + message);
                    }
                } else if (line.startsWith("ERROR:")) {
                    appendChat("Server error: " + line.substring(6));
                } else {
                    appendChat("Server: " + line);
                }
            }
        } catch (IOException e) {
            appendChat("Disconnected from server: " + e.getMessage());
        } finally {
            SwingUtilities.invokeLater(this::disconnect);
        }
    }

    private void updateClientList(String payload) {
        clientNames.clear();
        if (!payload.isEmpty()) {
            String[] entries = payload.split(",");
            for (String entry : entries) {
                String[] parts = entry.split("=", 2);
                if (parts.length == 2) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        clientNames.put(id, name);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        updateTargetList();
    }

    private void updateTargetList() {
        SwingUtilities.invokeLater(() -> {
            targetCombo.removeAllItems();
            targetCombo.addItem("Select target");
            for (Map.Entry<Integer, String> entry : clientNames.entrySet()) {
                if (entry.getKey() != clientId) {
                    targetCombo.addItem(entry.getKey() + ": " + entry.getValue());
                }
            }
        });
    }

    private void sendMessage(ActionEvent event) {
        if (writer == null) {
            appendChat("Not connected to server.");
            return;
        }
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        String selected = (String) targetCombo.getSelectedItem();
        if (selected == null || selected.equals("Select target")) {
            appendChat("Please select a target client.");
            return;
        }
        int targetId;
        try {
            String idToken = selected.split(":", 2)[0].trim();
            targetId = Integer.parseInt(idToken);
        } catch (NumberFormatException e) {
            appendChat("Invalid target selection.");
            return;
        }
        writer.println("SEND:" + targetId + ":" + text);
        appendChat("To " + selected + ": " + text);
        messageField.setText("");
    }

    private void appendChat(String text) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(text + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}
