package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NameServiceClient implements AutoCloseable {
    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;

    public NameServiceClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String sendCommand(String command) throws IOException {
        writer.println(command);
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        IOException closeException = null;
        try {
            reader.close();
        } catch (IOException e) {
            closeException = e;
        }
        writer.close();
        if (writer.checkError() && closeException == null) {
            closeException = new IOException("I/O error detected while closing client output stream");
        }
        try {
            socket.close();
        } catch (IOException e) {
            if (closeException == null) {
                closeException = e;
            }
        }
        if (closeException != null) {
            throw closeException;
        }
    }
}
