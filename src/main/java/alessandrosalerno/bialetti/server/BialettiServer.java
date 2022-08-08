package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BialettiServer {
    /*
     * The port on which the server is hosted
     */
    protected final int serverPort;
    /*
     * The BialettiClientHandler class that implements
     * the necessary handler methods
     */
    protected final BialettiClientHandler bialettiClientHandler;
    /*
     *  List of connected clients
     */
    protected List<BialettiConnection> connectedClients;
    /*
     * Thread on which the server's listen method is run
     */
    protected Thread listenThread;

    /*
     * Default constructor (Used with anonymous classes)
     * @param port The port on which the server is hosted
     * @param clientHandler BialettiClientHandler instance
     */
    @SuppressWarnings("unchecked")
    public BialettiServer(int port, BialettiClientHandler clientHandler) {
        serverPort            = port;
        bialettiClientHandler = clientHandler;
        connectedClients      = new ArrayList<>();

        // Create thread to listen to incoming requests
        listenThread = new Thread() {
            @Override
            public void run() {
                listen();
            }
        };

        listenThread.start();
    }

    /*
     * Secondary constructor (Used with named classes)
     * @param port The port on which the server is hosted
     * @param handlerClass The BialettiClientHandler class itself
     */
    public BialettiServer(int port, Class<BialettiClientHandler> handlerClass) throws Exception {
        this(port, handlerClass.getDeclaredConstructor().newInstance());
    }

    /*
     * Listens for incoming connections
     */
    protected void listen() {
        // Create server socket
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            // Accept connections forever
            while (true) {
                // Wait for a client to connect and instantiate a BialettiConnection for it
                BialettiConnection newClient  = new BialettiConnection(serverSocket.accept());

                // Create reference to BialettiServer instance
                BialettiServer thisServer = this;

                // Spawn new thread
                Thread clientThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            // Call initial connection method
                            bialettiClientHandler.onConnect(newClient, thisServer);

                            // Main handler loop
                            while (true) {
                                // Call handler method
                                bialettiClientHandler.handle(newClient, thisServer);
                            }
                        }

                        // Exception handler
                        catch (Exception e) {
                            try {
                                // Close socket connection
                                thisServer.closeConnection(newClient);
                            }

                            // Exception handler
                            catch (IOException ioException) {
                                System.out.println("[-] Unable to close connection");
                                ioException.printStackTrace();
                            }

                            // Call handler method
                            bialettiClientHandler.onClose(newClient, thisServer);
                        }
                    }
                };

                // Append client to the list of connected clients and start the thread
                connectedClients.add(newClient);
                clientThread.start();
            }
        }

        // Exception handler
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Closes a connection to a client
     * @param connection The BialettiConnection instance of the client
     */
    public void closeConnection(BialettiConnection connection) throws IOException {
        connection.getSocket().close();
        connectedClients.remove(connection);
        bialettiClientHandler.onClose(connection, this);
    }

    /*
     * Getter for the server's host port
     */
    public int getPort() { return serverPort; }
}
