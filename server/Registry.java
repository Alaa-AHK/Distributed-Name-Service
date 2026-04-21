package server;

import java.util.concurrent.ConcurrentHashMap;

public class Registry {
    private final ConcurrentHashMap<String, String> nameToIp = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> ipToName = new ConcurrentHashMap<>();

    public synchronized String register(String name, String ip) {
        if (name == null || ip == null || name.trim().isEmpty() || ip.trim().isEmpty()) {
            return "ERROR: Invalid Arguments";
        }

        if (nameToIp.containsKey(name)) {
            return "ERROR: Service Name Already Registered";
        }

        if (ipToName.containsKey(ip)) {
            return "ERROR: IP Already Registered";
        }

        nameToIp.put(name, ip);
        ipToName.put(ip, name);
        return "OK: Registered";
    }

    public synchronized String resolve(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "ERROR: Not Found";
        }

        String ip = nameToIp.get(name);
        if (ip == null) {
            return "ERROR: Not Found";
        }

        return "OK: " + ip;
    }

    public synchronized String deregister(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "ERROR: Service Not Found";
        }

        String ip = nameToIp.remove(name);
        if (ip == null) {
            return "ERROR: Service Not Found";
        }

        ipToName.remove(ip);
        return "OK: Deregistered";
    }
}
