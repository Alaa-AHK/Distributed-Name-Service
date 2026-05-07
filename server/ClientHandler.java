package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientHandler implements Runnable {

    private final Socket clientSocket;   // The connection to this specific client
    private final Registry registry;     // Shared registry (same object across all threads)

    public ClientHandler(Socket clientSocket, Registry registry) {
        this.clientSocket = clientSocket;
        this.registry = registry;
    }

   
    @Override
    public void run() {
        System.out.println("[Server] New client connected: " + clientSocket.getInetAddress());

        try (
            
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
            );
            
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;

            
            while ((message = in.readLine()) != null) {
                System.out.println("[Server] Received: " + message);

                String response = processMessage(message.trim());

                out.println(response);
                System.out.println("[Server] Sent: " + response);
            }

        } catch (Exception e) {
            System.out.println("[Server] Client disconnected: " + e.getMessage());
        }

        System.out.println("[Server] Client handler finished.");
    }

    private String processMessage(String message) {

        String[] parts = message.split(" ", 3);

        String command = parts[0].toUpperCase();

        switch (command) {

            case "REGISTER":
                if (parts.length < 3) {
                    return "ERROR: Usage: REGISTER <serviceName> <ipAddress>";
                }
                return registry.register(parts[1], parts[2]);

            case "RESOLVE":
                if (parts.length < 2) {
                    return "ERROR: Usage: RESOLVE <serviceName>";
                }
                return registry.resolve(parts[1]);

            case "DEREGISTER":
                if (parts.length < 2) {
                    return "ERROR: Usage: DEREGISTER <serviceName>";
                }
                return registry.deregister(parts[1]);

            default:
                return "ERROR: Unknown command. Use REGISTER, RESOLVE, or DEREGISTER";
        }
    }
}
