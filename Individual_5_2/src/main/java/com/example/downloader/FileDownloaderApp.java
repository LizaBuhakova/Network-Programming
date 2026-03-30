package com.example.downloader;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class FileDownloaderApp extends JFrame implements DownloadTask.StatusCallback {
    private final JTextArea urlText = new JTextArea(5, 60);
    private final JButton startButton = new JButton("Start Download");
    private final JButton stopButton = new JButton("Stop");
    private final DefaultTableModel model;
    private final JTable table;
    private final DownloadManager manager;
    public FileDownloaderApp() {
        super("Individual #5.2 Swing-based downloader");
        String[] columns = {"URL", "File", "Status", "Progress", "Speed"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;}};
        table = new JTable(model);
        setLayout(new BorderLayout(8, 8));
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBorder(BorderFactory.createTitledBorder("One URL per line"));
        top.add(new JScrollPane(urlText), BorderLayout.CENTER);
        JPanel ctl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ctl.add(startButton);
        ctl.add(stopButton);
        top.add(ctl, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        stopButton.setEnabled(false);
        startButton.addActionListener(e -> start());
        stopButton.addActionListener(e -> stop());
        manager = new DownloadManager(4);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);}
    private void start() {
        if (manager.isRunning()) {
            JOptionPane.showMessageDialog(this, "Downloading in progress", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;}
        createDownloadFolder();
        List<DownloadTask> tasks = new ArrayList<>();
        int rowStart = model.getRowCount();
        for (String line : urlText.getText().split("\\r?\\n")) {
            String url = line.trim();
            if (url.isEmpty()) continue;
            String filename = FileUtils.safeFilename(url);
            model.addRow(new Object[]{url, filename, "Queued", "0%", "0 KB/s"});
            tasks.add(new DownloadTask(url, filename, this));}
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No URLs provided", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;}
        manager.loadTasks(tasks, rowStart);
        manager.start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);}
    private void stop() {
        manager.stop();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);}
    private void createDownloadFolder() {}
    @Override
    public void onStatus(int index, String status) {
        SwingUtilities.invokeLater(() -> model.setValueAt(status, index, 2));}
    @Override
    public void onProgress(int index, String progress) {
        SwingUtilities.invokeLater(() -> model.setValueAt(progress, index, 3));}
    @Override
    public void onSpeed(int index, String speed) {
        SwingUtilities.invokeLater(() -> model.setValueAt(speed, index, 4));}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileDownloaderApp().setVisible(true));}}
