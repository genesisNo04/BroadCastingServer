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
            //client will create a socket for its own with host and port
            //Need hosst because it need to actively connecting to a host
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Connected to boardcast server.");

            //Client also create a thread to listen from the socket
            new Thread(() -> listen(socket)).start();
            //Get a scanner so it can write the message from user input
            Scanner scanner = new Scanner(System.in);
            //Get the outputstream so it can send the message to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                //get in infinite loop and every time client write a message it will send to the server
                String msg = scanner.nextLine();
                out.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to server.");
        }
    }

    private static void listen(Socket socket) {
        //This is use to listen to the broadcast message from the server
        //An inputstream from the server wrapped in Buffereader
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            //While  there is still message we will print it on the client screen
            while ((msg = in.readLine()) != null) {
                System.out.println("> " + msg);
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        }
    }
}
