===================================================
  GROUP X — DISTRIBUTED NAME SERVICE
  Run Instructions
===================================================

FOLDER STRUCTURE:
  Group_X_SourceCode/
  ├── server/
  │   ├── Registry.java
  │   ├── ClientHandler.java
  │   └── NameServiceServer.java
  ├── client/
  │   ├── NameServiceClient.java
  │   └── NodeSimulator.java
  └── README.txt

===================================================
  STEP 1: COMPILE
===================================================

From inside the Group_X_SourceCode/ folder, run:

  Windows:
    javac server\*.java client\*.java

  Mac/Linux:
    javac server/*.java client/*.java

This compiles all .java files and creates .class files.

===================================================
  STEP 2: START THE SERVER
===================================================

Open a terminal and run:

  Windows:
    java server.NameServiceServer

  Mac/Linux:
    java server.NameServiceServer

You should see:
  ===========================================
    Name Service Server started on port 5000
    Waiting for client connections...
  ===========================================

Leave this terminal open. The server runs until you press Ctrl+C.

===================================================
  STEP 3: RUN THE CLIENT SIMULATOR
===================================================

Open a SECOND terminal (keep the server running) and run:

  Windows:
    java client.NodeSimulator

  Mac/Linux:
    java client.NodeSimulator

This runs all sequential and concurrent tests.

===================================================
  TESTING ON SEPARATE MACHINES
===================================================

To run the server and client on different machines:

1. Find the server machine's IP address:
   - Windows: run  ipconfig  → look for IPv4 Address
   - Linux/Mac: run  ifconfig  or  ip addr

2. In NodeSimulator.java, change this line:
     private static final String SERVER_HOST = "localhost";
   to:
     private static final String SERVER_HOST = "192.168.X.X"; // server's IP

3. Make sure port 5000 is open on the server machine's firewall.

===================================================
  MANUAL TESTING WITH TELNET
===================================================

You can also manually test the protocol using telnet:

  telnet localhost 5000

Then type commands directly:
  REGISTER DatabaseNode 192.168.1.50
  RESOLVE DatabaseNode
  DEREGISTER DatabaseNode

===================================================
  PROTOCOL REFERENCE
===================================================

Command                              Server Response
-------                              ---------------
REGISTER <name> <ip>                 OK: Registered
                                     ERROR: Service Name Already Registered
                                     ERROR: IP Already Registered

RESOLVE <name>                       OK: <ip>
                                     ERROR: Not Found

DEREGISTER <name>                    OK: Deregistered
                                     ERROR: Service Not Found
