package client;

/**
 * NodeSimulator.java
 *
 * This is the DEMO/TEST program. Run this after starting NameServiceServer.
 *
 * It simulates multiple network nodes connecting simultaneously to the
 * Name Service and performing operations. This proves:
 *   ✓ REGISTER works
 *   ✓ RESOLVE works
 *   ✓ DEREGISTER works
 *   ✓ IP conflict detection works (Requirement D)
 *   ✓ Concurrent access works (multiple threads at once)
 *
 * HOW CONCURRENCY IS DEMONSTRATED:
 *   We create 3 threads. Each thread creates its own client and
 *   performs operations. All 3 start at nearly the same time using
 *   thread.start() in quick succession, so the server must handle
 *   them simultaneously.
 */
public class NodeSimulator {

    // Server details — change host if running on a different machine
    private static final String SERVER_HOST = "localhost";
    private static final int    SERVER_PORT = 5000;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("============================================");
        System.out.println("  Node Simulator Starting");
        System.out.println("  Connecting to " + SERVER_HOST + ":" + SERVER_PORT);
        System.out.println("============================================\n");

        // ----------------------------------------------------------------
        // PART 1: Sequential tests — easy to follow, proves basic protocol
        // ----------------------------------------------------------------
        System.out.println("--- PART 1: Sequential Tests ---\n");
        runSequentialTests();

        // Give server a moment between test sections
        Thread.sleep(500);

        // ----------------------------------------------------------------
        // PART 2: Concurrent tests — multiple threads at the same time
        // ----------------------------------------------------------------
        System.out.println("\n--- PART 2: Concurrent Tests (3 threads) ---\n");
        runConcurrentTests();

        System.out.println("\n============================================");
        System.out.println("  All tests complete.");
        System.out.println("============================================");
    }

    // -----------------------------------------------------------------------
    // SEQUENTIAL TESTS
    // -----------------------------------------------------------------------
    /**
     * Runs a series of tests one after another in a single thread.
     * Easy to read and understand the protocol behavior.
     */
    private static void runSequentialTests() {
        try {
            NameServiceClient client = new NameServiceClient(SERVER_HOST, SERVER_PORT, "TestNode");

            System.out.println(">> Test 1: Register a service");
            String r1 = client.register("DatabaseNode", "192.168.1.50");
            assert r1.equals("OK: Registered") : "Expected OK";

            System.out.println("\n>> Test 2: Resolve that service");
            String r2 = client.resolve("DatabaseNode");
            assert r2.equals("OK: 192.168.1.50") : "Expected IP";

            System.out.println("\n>> Test 3: Try to register SAME IP (should fail — Requirement D)");
            String r3 = client.register("AnotherNode", "192.168.1.50");
            assert r3.equals("ERROR: IP Already Registered") : "Expected IP conflict error";

            System.out.println("\n>> Test 4: Try to register SAME name (should fail)");
            String r4 = client.register("DatabaseNode", "10.0.0.99");
            assert r4.equals("ERROR: Service Name Already Registered") : "Expected name conflict";

            System.out.println("\n>> Test 5: Resolve a service that doesn't exist");
            String r5 = client.resolve("GhostService");
            assert r5.equals("ERROR: Not Found") : "Expected not found";

            System.out.println("\n>> Test 6: Deregister the service");
            String r6 = client.deregister("DatabaseNode");
            assert r6.equals("OK: Deregistered") : "Expected OK";

            System.out.println("\n>> Test 7: Resolve after deregister (should be gone)");
            String r7 = client.resolve("DatabaseNode");
            assert r7.equals("ERROR: Not Found") : "Expected not found after deregister";

            System.out.println("\n>> Test 8: Re-register with the same IP (now free again)");
            String r8 = client.register("NewDatabaseNode", "192.168.1.50");
            assert r8.equals("OK: Registered") : "Expected OK after IP freed";

            client.close();
            System.out.println("\n[Sequential tests PASSED]");

        } catch (Exception e) {
            System.out.println("[ERROR in sequential tests]: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------
    // CONCURRENT TESTS
    // -----------------------------------------------------------------------
    /**
     * Launches 3 threads simultaneously, each acting as a different network node.
     * This tests that the server handles concurrent connections without crashing
     * or corrupting data.
     *
     * Each thread runs a Runnable (a lambda with the client logic).
     * All three are started in quick succession so they overlap on the server.
     */
    private static void runConcurrentTests() throws InterruptedException {

        // Node A: registers a service and resolves it
        Thread nodeA = new Thread(() -> {
            try {
                NameServiceClient client = new NameServiceClient(SERVER_HOST, SERVER_PORT, "NodeA");
                client.register("AuthService", "10.0.0.1");
                Thread.sleep(100); // simulate some processing time
                client.resolve("AuthService");
                client.close();
            } catch (Exception e) {
                System.out.println("[NodeA ERROR]: " + e.getMessage());
            }
        });

        // Node B: registers a different service and deregisters it
        Thread nodeB = new Thread(() -> {
            try {
                NameServiceClient client = new NameServiceClient(SERVER_HOST, SERVER_PORT, "NodeB");
                client.register("PaymentService", "10.0.0.2");
                Thread.sleep(50);
                client.resolve("AuthService"); // looks up Node A's service
                client.deregister("PaymentService");
                client.close();
            } catch (Exception e) {
                System.out.println("[NodeB ERROR]: " + e.getMessage());
            }
        });

        // Node C: tries to steal Node A's IP (should be rejected)
        Thread nodeC = new Thread(() -> {
            try {
                NameServiceClient client = new NameServiceClient(SERVER_HOST, SERVER_PORT, "NodeC");
                // This should fail: 10.0.0.1 is already used by AuthService
                client.register("RogueService", "10.0.0.1");
                // This should succeed: fresh IP
                client.register("LoggingService", "10.0.0.3");
                client.close();
            } catch (Exception e) {
                System.out.println("[NodeC ERROR]: " + e.getMessage());
            }
        });

        // Start all three threads nearly simultaneously
        nodeA.start();
        nodeB.start();
        nodeC.start();

        // Wait for all three to finish before printing summary
        nodeA.join();
        nodeB.join();
        nodeC.join();

        System.out.println("\n[Concurrent tests COMPLETE — check output above for results]");
    }
}
