package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
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

        //This is to create a new socket Server and reserve the port 8080
        //This is use for listening to client when they connect
        //basically a listener
        //This will have the server address and the server port and a queue for incoming connection requests
        // ex: Server side socket:
        //   Local:   192.168.1.10:8080
        //   Remote:  192.168.1.50:53124
        // the server bind to a PORT and listen on the port, by default it listen on all the network interface, so does not need host
        //Does not connect to anyone, it just wait
        //Host is implicitly this machine
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                //at this point it will pause the program until a client is connected
                //When client connect a new socket will be created
                //Contains server addrress, port, client IP client random ephemeral port
                Socket socket = serverSocket.accept();

                //This print out the address of the client address who connect to the server
                System.out.println("Client connected: " + socket.getRemoteSocketAddress());

                //Idk what printwriter do but my guess is that this create a writer that can write to client
                //Write data to client through socket
                //Every socket has an outputstream and inputstream
                //inputstream data coming from client
                //outputstream data going to client
                //PrintWriter is a wrapper class make it easy to write text
                //autoFlush: forces data to be sent immediately when we call println
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // and this add the printwrite to the client out put so that we can write message to client
                //every time there is a new msg, server will go through all of the printwriter and send the message to each client
                clientOutputs.add(out);

                //start a new thread for each client
                //this thread handle handleClient(socket, out)
                new Thread(() -> handleClient(socket, out)).start();

                new Thread(() -> {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        String serverMessage = "[SERVER]" + scanner.nextLine();
                        broadcast(serverMessage);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //While thread is up it will enter the while loop
    //this run continuously and read message one-by-one in a loop
    private static void handleClient(Socket socket, PrintWriter out) {
        //first the server will try to create a bufferredReader to read from the inputstream of the socket
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            //if the reader is successfully created it will get in the while loop
            //thread will wait for the message
            //when client send a msg, readline will read it
            //when client send null by close the connection or the socket is shutdown from client side and it end the loop
            while ((message = in.readLine()) != null) {
                broadcast(message);
            }
            //this catch block only when network error, client disconnect abrubtly or server has problem reading the stream
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
