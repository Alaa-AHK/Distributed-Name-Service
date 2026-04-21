package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NameServiceServer {
    private static final int PORT = 5000;

    public static void main(String[] args) {
        Registry registry = new Registry();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("===========================================");
            System.out.println("  Name Service Server started on port 5000");
            System.out.println("  Waiting for client connections...");
            System.out.println("===========================================");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new ClientHandler(clientSocket, registry));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
