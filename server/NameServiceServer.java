package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NameServiceServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENT_THREADS = 50;

    public static void main(String[] args) {
        Registry registry = new Registry();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENT_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("===========================================");
            System.out.println("  Name Service Server started on port 5000");
            System.out.println("  Waiting for client connections...");
            System.out.println("===========================================");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket, registry));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            executorService.shutdownNow();
        }
    }
}
