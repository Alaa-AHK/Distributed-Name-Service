package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * LIFECYCLE:
 *   1. Create client → connects to server
 *   2. Call register/resolve/deregister → sends message, gets response
 *   3. Call close() → disconnects cleanly
 */
public class NameServiceClient {

    private Socket socket;          // The connection to the server
    private PrintWriter out;        // For sending messages to the server
    private BufferedReader in;      // For receiving responses from the server
    private final String nodeName;  // For logging: who is this client?

    public NameServiceClient(String host, int port, String nodeName) throws Exception {
        this.nodeName = nodeName;

        // TCP connection
        // throws a ConnectException if server is not running or unreachable.
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("[" + nodeName + "] Connected to Name Service at " + host + ":" + port);
    }

    // PUBLIC METHODS: what NodeSimulator calls

    /**
     * Sends a REGISTER command to the server.
     * Message format: REGISTER <serviceName> <ipAddress>
     *
     * @return the server's response (e.g., "OK: Registered" or an error)
     */
    public String register(String serviceName, String ipAddress) throws Exception {
        return sendMessage("REGISTER " + serviceName + " " + ipAddress);
    }

    /**
     * Sends a RESOLVE command to the server.
     * Message format: RESOLVE <serviceName>
     *
     * @return "OK: <ipAddress>" or "ERROR: Not Found"
     */
    public String resolve(String serviceName) throws Exception {
        return sendMessage("RESOLVE " + serviceName);
    }

    /**
     * Sends a DEREGISTER command to the server.
     * Message format: DEREGISTER <serviceName>
     *
     * @return "OK: Deregistered" or an error message
     */
    public String deregister(String serviceName) throws Exception {
        return sendMessage("DEREGISTER " + serviceName);
    }

    // PRIVATE HELPER METHODS

    /**
     * The core send-and-receive method.
     *
     * 1. out.println() sends the message as a text line to the server
     * 2. in.readLine() waits (blocks) until the server sends a response line
     * 3. Returns the response string
     */
    private String sendMessage(String message) throws Exception {
        System.out.println("[" + nodeName + "] Sending:  " + message);

        // Send the message to the server
        out.println(message);

        // Wait for server's response
        String response = in.readLine();
        System.out.println("[" + nodeName + "] Received: " + response);

        return response;
    }

    /**
     * Cleanly closes the connection to the server.
     * Always call this when you're done with the client.
     */
    public void close() throws Exception {
        socket.close();
        System.out.println("[" + nodeName + "] Disconnected.");
    }
}
