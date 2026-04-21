package server;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private final Map<String, String> nameToIp = new HashMap<String, String>();
    private final Map<String, String> ipToName = new HashMap<String, String>();

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
