package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    //define the port that the server will run on: 8080
    //the server will listen to client on port 8080
    private static final int PORT = 8080;
    //Create a thread-safe set where each item is a PrintWriter connected to one client
    //Each client will have each own output stream
    //output stream is what server use to send message to client
    //When one client send a message, the server will loop through this and get all the printwriter and send that message to all clients
    //add a PrintWrite when client connect and remove when client disconnect
    //This is threadsafe, normal one is not
    private static final Set<PrintWriter> clientOutputs = ConcurrentHashMap.newKeySet();

    public static void start() {
        //Just a message to show the server is starting
        System.out.println("Broadcast server started on port: " + PORT);

        //This try block is to reserve and open the connect with port 8080
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                //This is to create a new socket and mean the server is starting to accept connection
                Socket socket = serverSocket.accept();

                //This print out the address of the client address who connect to the server
                System.out.println("Client connected: " + socket.getRemoteSocketAddress());

                //Idk what printwriter do but my guess is that this create a writer that can write to client
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // and this add the printwrite to the client out put so that we can write message to client
                clientOutputs.add(out);
                //idk what this do
                new Thread(() -> handleClient(socket, out)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, PrintWriter out) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;

            while ((message = in.readLine()) != null) {
                broadcast(message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
        } finally {
            clientOutputs.remove(out);
            try {
                socket.close();
            } catch (IOException ignored) {

            }
        }
    }


    private static void broadcast(String msg) {
        for (PrintWriter writer : clientOutputs) {
            writer.println(msg);
        }

        System.out.println("Broadcasted: " + msg);
    }
}
