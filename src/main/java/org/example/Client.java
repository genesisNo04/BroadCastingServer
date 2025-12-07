package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void connect() {
        try {
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Connected to boardcast server.");

            new Thread(() -> listen(socket)).start();

            Scanner scanner = new Scanner(System.in);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String msg = scanner.nextLine();
                out.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
        }
    }

    private static void listen(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("> " + msg);
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        }
    }
}
