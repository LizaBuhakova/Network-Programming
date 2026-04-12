package ua.network.udp.server;
import ua.network.udp.common.ClientInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class ClientRegistry {
    private final Path filePath;
    private final Map<String, ClientInfo> clients = new LinkedHashMap<>();
    public ClientRegistry(Path filePath) {
        this.filePath = filePath;}
    public synchronized void load() throws IOException {
        clients.clear();
        List<String> lines = Files.readAllLines(filePath);
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;}
            ClientInfo client = ClientInfo.parse(line);
            clients.put(client.getId(), client);}}
    public synchronized Collection<ClientInfo> getAll() {
        return new ArrayList<>(clients.values());}
    public synchronized List<ClientInfo> getByIds(List<String> ids) {
        List<ClientInfo> result = new ArrayList<>();
        for (String id : ids) {
            ClientInfo info = clients.get(id);
            if (info == null) {
                throw new IllegalArgumentException("Client with id '" + id + "' was not found in file " + filePath);}
            result.add(info);}
        return result;}}