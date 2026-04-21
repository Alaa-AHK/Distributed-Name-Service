package server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * NameServiceServer.java
 *
 * This is the ENTRY POINT for the server. Run this class to start the Name Service.
 *
 * HOW A SERVER SOCKET WORKS:
 *
 *   ServerSocket binds to a port (like door number 5000 on this machine).
 *   It "listens" for incoming connections.
 *   When a client connects, accept() hands back a Socket object representing
 *   that specific client. The ServerSocket then goes back to listening.
 *
 * VISUAL ANALOGY:
 *
 *   ServerSocket = a receptionist at a front desk
 *   accept()     = receptionist greets a visitor and assigns them a handler
 *   ClientHandler= the handler takes the visitor to a private room
 *   Registry     = the shared filing cabinet all handlers access
 *
 * The main thread's only job is to sit in the accept() loop forever.
 * All real work is done by ClientHandler threads.
 */
public class NameServiceServer {

    // The port the server listens on.
    // Clients must connect to this same port number.
    private static final int PORT = 5000;

    public static void main(String[] args) {

        // Create ONE shared Registry object.
        // This same object is passed to every ClientHandler thread.
        // This is safe because Registry uses synchronized methods.
        Registry registry = new Registry();

        System.out.println("===========================================");
        System.out.println("  Name Service Server started on port " + PORT);
        System.out.println("  Waiting for client connections...");
        System.out.println("===========================================");

        // try-with-resources: ServerSocket auto-closes if something goes wrong
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // This loop runs FOREVER — the server never stops on its own.
            // Each iteration handles one new incoming client.
            while (true) {

                // accept() BLOCKS here — the main thread pauses and waits
                // until a client connects. Once a client connects, it returns
                // a Socket object representing that client's connection.
                Socket clientSocket = serverSocket.accept();

                // Create a handler for this client, passing:
                //   - the client's socket (to read/write to that specific client)
                //   - the shared registry (so it can register/resolve/deregister)
                ClientHandler handler = new ClientHandler(clientSocket, registry);

                // Start the handler in a NEW thread.
                // This is the key line that enables concurrency:
                // The main thread immediately loops back to accept() while
                // the new thread handles this client independently.
                new Thread(handler).start();
            }

        } catch (Exception e) {
            System.out.println("[Server] Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
