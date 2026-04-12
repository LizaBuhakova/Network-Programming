package ua.network.udp.common;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
public class ClientInfo {
    private final String id;
    private final InetAddress address;
    private final int port;
    public ClientInfo(String id, InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;}
    public String getId() {
        return id;}
    public InetAddress getAddress() {
        return address;}
    public int getPort() {
        return port;}
    public static ClientInfo parse(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid client record format: " + line);}
        String id = parts[0].trim();
        String host = parts[1].trim();
        int port;
        try {
            port = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port in record: " + line, e);}
        try {
            InetAddress address = InetAddress.getByName(host);
            return new ClientInfo(id, address, port);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid host in record: " + line, e);}}
    @Override
    public String toString() {
        return "ClientInfo{" +
                "id='" + id + '\'' +
                ", address=" + address.getHostAddress() +
                ", port=" + port +
                '}';}
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;}
        if (!(o instanceof ClientInfo)) {
            return false;}
        ClientInfo that = (ClientInfo) o;
        return port == that.port
                && Objects.equals(id, that.id)
                && Objects.equals(address, that.address);}
    @Override
    public int hashCode() {
        return Objects.hash(id, address, port);}}