package server;

import java.net.ServerSocket;
import java.net.Socket;

public class NameServiceServer {

    
    private static final int PORT = 5000;

    public static void main(String[] args) {

     
        Registry registry = new Registry();

        System.out.println("===========================================");
        System.out.println("  Name Service Server started on port " + PORT);
        System.out.println("  Waiting for client connections...");
        System.out.println("===========================================");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            
            while (true) {

               
                Socket clientSocket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(clientSocket, registry);

              
                new Thread(handler).start();
            }

        } catch (Exception e) {
            System.out.println("[Server] Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
