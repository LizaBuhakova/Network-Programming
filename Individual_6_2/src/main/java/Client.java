import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {
            System.out.print("Введіть ваше ім'я: ");
            String name = scanner.nextLine();
            out.println(name);
            System.out.println("Підключено до сервера. Введіть повідомлення у форматі 'одержувач:повідомлення' або 'exit' для виходу");
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);}
                } catch (IOException e) {
                    e.printStackTrace();}
            }).start();
            String message;
            while (!(message = scanner.nextLine()).equals("exit")) {
                out.println(message);}
        } catch (IOException e) {
            e.printStackTrace();}}}