package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Registry registry;

    public ClientHandler(Socket socket, Registry registry) {
        this.socket = socket;
        this.registry = registry;
    }

    @Override
    public void run() {
        try (
            Socket clientSocket = socket;
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String response = processCommand(line);
                writer.println(response);
            }
        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        }
    }

    private String processCommand(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return "ERROR: Invalid Command";
        }

        String[] parts = commandLine.trim().split("\\s+");
        String command = parts[0].toUpperCase();

        switch (command) {
            case "REGISTER":
                return parts.length == 3 ? registry.register(parts[1], parts[2]) : "ERROR: Invalid Command";
            case "RESOLVE":
                return parts.length == 2 ? registry.resolve(parts[1]) : "ERROR: Invalid Command";
            case "DEREGISTER":
                return parts.length == 2 ? registry.deregister(parts[1]) : "ERROR: Invalid Command";
            default:
                return "ERROR: Invalid Command";
        }
    }
}
