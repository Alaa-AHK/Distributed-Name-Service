package server;

import java.util.concurrent.ConcurrentHashMap;


public class Registry {

  
    private final ConcurrentHashMap<String, String> nameToIP = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, String> ipToName = new ConcurrentHashMap<>();

    public synchronized String register(String serviceName, String ipAddress) {

        if (nameToIP.containsKey(serviceName)) {
            return "ERROR: Service Name Already Registered";
        }

        if (ipToName.containsKey(ipAddress)) {
            return "ERROR: IP Already Registered";
        }

        nameToIP.put(serviceName, ipAddress);
        ipToName.put(ipAddress, serviceName);

        return "OK: Registered";
    }

    
    public String resolve(String serviceName) {
        String ip = nameToIP.get(serviceName);

        if (ip == null) {
            return "ERROR: Not Found";
        }

        return "OK: " + ip;
    }

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
