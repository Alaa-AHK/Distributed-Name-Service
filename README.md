# Distributed Name Service

A robust, Java-based Distributed Name Service demonstrating a client-server architecture. This system allows clients to register, resolve, and deregister node names to IP addresses seamlessly.

## Folder Structure

```text
.
├── client/
│   ├── NameServiceClient.java
│   └── NodeSimulator.java
├── server/
│   ├── ClientHandler.java
│   ├── NameServiceServer.java
│   └── Registry.java
└── README.md
```

## Getting Started

### 1. Compile the Code

Navigate to the project root directory and compile the source code:

**Windows**:
```cmd
javac server\*.java client\*.java
```

**Mac/Linux**:
```bash
javac server/*.java client/*.java
```

### 2. Start the Server

Open a terminal and start the `NameServiceServer`:

```bash
java server.NameServiceServer
```

*Expected Output:*
```text
===========================================
  Name Service Server started on port 5000
  Waiting for client connections...
===========================================
```
*Note: Leave this terminal open. The server will run until terminated (`Ctrl+C`).*

### 3. Run the Client Simulator

Open a **second** terminal and run the simulator to execute the sequential and concurrent tests:

```bash
java client.NodeSimulator
```

## Advanced Usage

### Testing on Separate Machines

To run the server and client on different machines instead of locally:

1. **Get the Server's IP Address**:
   - **Windows**: Run `ipconfig` (Look for the IPv4 Address).
   - **Mac/Linux**: Run `ifconfig` or `ip addr`.
2. **Update the Client Configuration**:
   Open `client/NodeSimulator.java` and modify the host variable:
   ```java
   private static final String SERVER_HOST = "192.168.X.X"; // Replace with your server's IP
   ```
3. **Firewall**: Ensure that port `5000` is open on the server machine's firewall.

### Manual Testing with Telnet

You can interact directly with the Name Service protocol using telnet:

```bash
telnet localhost 5000
```

Try the following manual commands:
- `REGISTER DatabaseNode 192.168.1.50`
- `RESOLVE DatabaseNode`
- `DEREGISTER DatabaseNode`

## Protocol Reference

| Command | Expected Server Response |
| :--- | :--- |
| `REGISTER <name> <ip>` | `OK: Registered`<br>`ERROR: Service Name Already Registered`<br>`ERROR: IP Already Registered` |
| `RESOLVE <name>` | `OK: <ip>`<br>`ERROR: Not Found` |
| `DEREGISTER <name>` | `OK: Deregistered`<br>`ERROR: Service Not Found` |
