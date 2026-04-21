package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ClientHandler.java
 *
 * This class handles ONE client connection from start to finish.
 * It runs in its own thread, so many of these can run simultaneously.
 *
 * HOW THREADING WORKS HERE:
 * When the server accepts a new client connection, it creates a new
 * ClientHandler object and starts it in a new Thread:
 *
 *     new Thread(new ClientHandler(socket, registry)).start();
 *
 * This means 10 clients = 10 ClientHandler threads running in parallel.
 * Each one handles its own client independently.
 *
 * This class implements Runnable, which means it has a run() method.
 * That run() method is what the thread executes.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;   // The connection to this specific client
    private final Registry registry;     // Shared registry (same object across all threads)

    /**
     * Constructor — just stores the socket and registry reference.
     * The real work happens in run().
     */
    public ClientHandler(Socket clientSocket, Registry registry) {
        this.clientSocket = clientSocket;
        this.registry = registry;
    }

    /**
     * run() is called automatically when the thread starts.
     *
     * What this method does:
     * 1. Opens a "reader" to receive text from the client
     * 2. Opens a "writer" to send text back to the client
     * 3. Reads messages in a loop until the client disconnects
     * 4. Parses each message and calls the correct registry method
     * 5. Sends the result back to the client
     */
    @Override
    public void run() {
        System.out.println("[Server] New client connected: " + clientSocket.getInetAddress());

        // try-with-resources: automatically closes the socket when done
        try (
            // BufferedReader reads text line by line from the client
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
            );
            // PrintWriter sends text lines back to the client
            // 'true' means auto-flush: message is sent immediately after println()
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;

            // Keep reading messages until the client closes the connection
            // readLine() returns null when the connection is closed
            while ((message = in.readLine()) != null) {
                System.out.println("[Server] Received: " + message);

                // Parse the message and get a response
                String response = processMessage(message.trim());

                // Send the response back to the client
                out.println(response);
                System.out.println("[Server] Sent: " + response);
            }

        } catch (Exception e) {
            System.out.println("[Server] Client disconnected: " + e.getMessage());
        }

        System.out.println("[Server] Client handler finished.");
    }

    // -----------------------------------------------------------------------
    // MESSAGE PARSER
    // -----------------------------------------------------------------------
    /**
     * Takes a raw text message from the client and figures out what to do.
     *
     * Protocol:
     *   REGISTER <serviceName> <ipAddress>   → calls registry.register()
     *   RESOLVE <serviceName>                → calls registry.resolve()
     *   DEREGISTER <serviceName>             → calls registry.deregister()
     *   anything else                        → returns an error
     *
     * We use split(" ", 3) to break the message into up to 3 parts:
     *   parts[0] = command   (e.g., "REGISTER")
     *   parts[1] = name      (e.g., "DatabaseNode")
     *   parts[2] = ip        (e.g., "192.168.1.50")  -- only for REGISTER
     */
    private String processMessage(String message) {

        // Split the message into words (max 3 parts)
        String[] parts = message.split(" ", 3);

        // parts[0] is always the command
        String command = parts[0].toUpperCase();

        switch (command) {

            case "REGISTER":
                // REGISTER needs exactly 3 parts: REGISTER <name> <ip>
                if (parts.length < 3) {
                    return "ERROR: Usage: REGISTER <serviceName> <ipAddress>";
                }
                return registry.register(parts[1], parts[2]);

            case "RESOLVE":
                // RESOLVE needs exactly 2 parts: RESOLVE <name>
                if (parts.length < 2) {
                    return "ERROR: Usage: RESOLVE <serviceName>";
                }
                return registry.resolve(parts[1]);

            case "DEREGISTER":
                // DEREGISTER needs exactly 2 parts: DEREGISTER <name>
                if (parts.length < 2) {
                    return "ERROR: Usage: DEREGISTER <serviceName>";
                }
                return registry.deregister(parts[1]);

            default:
                return "ERROR: Unknown command. Use REGISTER, RESOLVE, or DEREGISTER";
        }
    }
}
