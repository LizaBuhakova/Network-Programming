package com.example.downloader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
public class DownloadManager {
    private final ConcurrentHashMap<Integer, DownloadTask> tasks = new ConcurrentHashMap<>();
    private ExecutorService executor;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final int maxThreads;
    public DownloadManager(int maxThreads) {
        this.maxThreads = maxThreads;}
    public void loadTasks(List<DownloadTask> taskList, int indexOffset) {
        tasks.clear();
        int idx = indexOffset;
        for (DownloadTask task : taskList) {
            task.setIndex(idx);
            task.setCancelledFlag(cancelled);
            tasks.put(idx, task);
            idx++;}}
    public void start() {
        if (executor != null && !executor.isShutdown()) {
            return;}
        cancelled.set(false);
        executor = Executors.newFixedThreadPool(Math.min(maxThreads, Math.max(1, tasks.size())));
        for (DownloadTask task : tasks.values()) {
            executor.submit(task);}}
    public void stop() {
        cancelled.set(true);
        if (executor != null) {
            executor.shutdownNow();}}
    public boolean isRunning() {
        return executor != null && !executor.isShutdown();}
    public List<DownloadTask> getTasks() {
        return new ArrayList<>(tasks.values());}}
