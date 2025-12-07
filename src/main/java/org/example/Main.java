package org.example;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: ");
            System.out.println(" broadcast-server start");
            System.out.println(" broadcast-server connect");
            return;
        }

        switch (args[0]) {
            case "start":
                Server.start();
                break;
            case "connect":
                Client.connect();
                break;
            default:
                System.out.println("Unkown command: " + args[0]);
        }
    }
}