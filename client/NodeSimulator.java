package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class NodeSimulator {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        System.out.println("===== Running Sequential Tests =====");
        runSequentialTests();

        System.out.println();
        System.out.println("===== Running Concurrent Tests =====");
        runConcurrentTests();
    }

    private static void runSequentialTests() {
        try (NameServiceClient client = new NameServiceClient(SERVER_HOST, SERVER_PORT)) {
            printResult("REGISTER AppNode 10.0.0.1", client.sendCommand("REGISTER AppNode 10.0.0.1"));
            printResult("REGISTER AppNode 10.0.0.2", client.sendCommand("REGISTER AppNode 10.0.0.2"));
            printResult("REGISTER DataNode 10.0.0.1", client.sendCommand("REGISTER DataNode 10.0.0.1"));
            printResult("RESOLVE AppNode", client.sendCommand("RESOLVE AppNode"));
            printResult("RESOLVE UnknownNode", client.sendCommand("RESOLVE UnknownNode"));
            printResult("DEREGISTER AppNode", client.sendCommand("DEREGISTER AppNode"));
            printResult("DEREGISTER AppNode", client.sendCommand("DEREGISTER AppNode"));
        } catch (IOException e) {
            System.err.println("Sequential test error: " + e.getMessage());
        }
    }

    private static void runConcurrentTests() {
        int clients = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(clients);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < clients; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                try (NameServiceClient client = new NameServiceClient(SERVER_HOST, SERVER_PORT)) {
                    startLatch.await();
                    String name = "Node" + index;
                    String ip = "10.0.1." + index;

                    printResult("REGISTER " + name + " " + ip, client.sendCommand("REGISTER " + name + " " + ip));
                    printResult("RESOLVE " + name, client.sendCommand("RESOLVE " + name));
                    printResult("DEREGISTER " + name, client.sendCommand("DEREGISTER " + name));
                } catch (Exception e) {
                    System.err.println("Concurrent client error: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });

            threads.add(thread);
            thread.start();
        }

        startLatch.countDown();

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Concurrent tests interrupted");
        }
    }

    private static void printResult(String command, String response) {
        System.out.println(command + " -> " + response);
    }
}
