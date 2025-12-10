package org.example;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ");
            System.out.println(" broadcast-server start");
            System.out.println(" broadcast-server connect");
            return;
        }

        if (!args[0].equals("broadcast-server")) {
            System.out.println("Unknown command: " + args[0]);
            return;
        }

        switch (args[1]) {
            case "start":
                Server.start();
                break;
            case "connect":
                Client.connect();
                break;
            default:
                System.out.println("Unknown option: " + args[1]);
        }
    }
}