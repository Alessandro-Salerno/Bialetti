package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;

import java.net.ServerSocket;
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
            // Wait for a client to connect and instantiate a BialettiConnection for it
            BialettiConnection newClient  = new BialettiConnection(serverSocket.accept());

            // Create atomic references
            AtomicReference<BialettiConnection> atomicClient     = new AtomicReference<>(newClient);
            AtomicReference<BialettiClientHandler> atomicHandler = new AtomicReference<>(bialettiClientHandler);
            AtomicReference<BialettiServer> atomicServer         = new AtomicReference<>(this);

            // Spawn new thread
            Thread clientThread = new Thread() {
                @Override
                public void run() {
                    atomicHandler.get().onConnect(atomicClient.get(), atomicServer.get());
                    while (true) {
                        atomicHandler.get().handle(atomicClient.get(), atomicServer.get());
                    }
                }
            };

            // Append client to the list of connected clients and start the thread
            connectedClients.add(newClient);
            clientThread.start();
        }

        // Exception handler
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Getter for the server's host port
     */
    public int getPort() { return serverPort; }
}
