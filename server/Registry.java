package server;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry.java
 *
 * This class is the "brain" of the Name Service.
 * It holds the actual directory that maps service names to IP addresses.
 *
 * Think of it like a phone book:
 *   - Service Name = person's name  (e.g., "DatabaseNode")
 *   - IP Address   = phone number   (e.g., "192.168.1.50")
 *
 * WHY ConcurrentHashMap?
 * A regular HashMap is NOT thread-safe. If two client threads try to
 * write to it at the same time, data can get corrupted or lost.
 * ConcurrentHashMap handles multiple threads reading/writing safely.
 */
public class Registry {

    // The directory: maps "ServiceName" -> "IPAddress"
    // Example entry: "DatabaseNode" -> "192.168.1.50"
    private final ConcurrentHashMap<String, String> nameToIP = new ConcurrentHashMap<>();

    // A reverse map: maps "IPAddress" -> "ServiceName"
    // This is how we quickly check if an IP is already in use (Requirement D).
    // Example entry: "192.168.1.50" -> "DatabaseNode"
    private final ConcurrentHashMap<String, String> ipToName = new ConcurrentHashMap<>();

    // -----------------------------------------------------------------------
    // REGISTER
    // -----------------------------------------------------------------------
    /**
     * Registers a new service name with its IP address.
     *
     * The tricky part here is Requirement D: no two services can share the
     * same IP. We must check AND insert atomically — meaning no other thread
     * can sneak in between our check and our insert.
     *
     * We use the keyword 'synchronized' on this method.
     * This means: only ONE thread can execute this method at a time.
     * Other threads wait at the door until the current one finishes.
     *
     * @param serviceName  e.g. "DatabaseNode"
     * @param ipAddress    e.g. "192.168.1.50"
     * @return a response string to send back to the client
     */
    public synchronized String register(String serviceName, String ipAddress) {

        // Check 1: Is this service name already registered?
        if (nameToIP.containsKey(serviceName)) {
            return "ERROR: Service Name Already Registered";
        }

        // Check 2: Is this IP already used by another service? (Requirement D)
        if (ipToName.containsKey(ipAddress)) {
            return "ERROR: IP Already Registered";
        }

        // All clear — store in both maps
        nameToIP.put(serviceName, ipAddress);
        ipToName.put(ipAddress, serviceName);

        return "OK: Registered";
    }

    // -----------------------------------------------------------------------
    // RESOLVE
    // -----------------------------------------------------------------------
    /**
     * Looks up the IP address for a given service name.
     * This is the core "query" operation — like looking up a name in a phone book.
     *
     * No synchronization needed here because ConcurrentHashMap handles
     * concurrent reads safely on its own.
     *
     * @param serviceName  e.g. "DatabaseNode"
     * @return the IP address, or an error if not found
     */
    public String resolve(String serviceName) {
        String ip = nameToIP.get(serviceName);

        if (ip == null) {
            return "ERROR: Not Found";
        }

        return "OK: " + ip;
    }

    // -----------------------------------------------------------------------
    // DEREGISTER
    // -----------------------------------------------------------------------
    /**
     * Removes a service from the registry entirely.
     * We must remove from BOTH maps to keep them in sync.
     *
     * Also synchronized because we're modifying both maps together —
     * we don't want another thread reading a half-deleted entry.
     *
     * @param serviceName  e.g. "DatabaseNode"
     * @return success or error message
     */
    public synchronized String deregister(String serviceName) {

        // First check the name exists
        String ip = nameToIP.get(serviceName);

        if (ip == null) {
            return "ERROR: Service Not Found";
        }

        // Remove from both maps
        nameToIP.remove(serviceName);
        ipToName.remove(ip);

        return "OK: Deregistered";
    }
}
