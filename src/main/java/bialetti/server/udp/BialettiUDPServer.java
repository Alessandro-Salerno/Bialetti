package bialetti.server.udp;

import bialetti.annotations.BialettiHandleMethod;
import bialetti.connection.udp.BialettiUDPServerConnection;
import bialetti.server.BialettiServer;
import bialetti.server.BialettiServerExceptionHandler;
import bialetti.util.ObjectUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * A Bialetti UDP Server
 * @author Alessandro-Salerno
 */
public abstract class BialettiUDPServer extends BialettiServer {
    /**
     * The connection on which the server listens for incoming requests
     * and sends outgoing replies
     */
    private final BialettiUDPServerConnection serverConnection;
    /**
     * The exception handler
     */
    private final BialettiServerExceptionHandler exceptionHandler;
    /**
     * The list of all threads
     */
    private final List<BialettiUDPServerThread> threads;

    /**
     * Constructor
     * @param port the port on which to open the server
     * @param sxh the exception handler
     */
    public BialettiUDPServer(int port, BialettiServerExceptionHandler sxh) {
        super(port);

        exceptionHandler = sxh;
        threads          = new ArrayList<>();

        serverConnection = openServerSocket();
        start();
    }

    /**
     * Starts the server
     */
    @Override
    public void start() {
        // Get the thread on which the method is running
        Thread startThread = Thread.currentThread();

        /*
         * Spawn server threads for each method with the BialettiHandleMethod annotation
         * Uses internal iteration
         */
        new ObjectUtility(this).forEachMethodWithAnnotation(BialettiHandleMethod.class,
                                                               method -> {
            BialettiUDPServerThread thread
                    = new BialettiUDPServerThread(method,
                                                  BialettiUDPServer.this,
                                                  exceptionHandler) {
                @Override
                public void run() {
                    try { startThread.join(); }
                    catch (InterruptedException ignored) { }

                    super.run();
                }
            };

            // Start the thread and add it to the list of threads
            thread.start();
            threads.add(thread);
        });

        // Create a dummy thread to keep thee server alive
        new Thread(() -> { while (!Thread.interrupted()) { } }).start();

        try { onStart(); }
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
        }
    }

    /**
     * Stops the server
     */
    @Override
    public void stop() {
        // Stop all threads
        threads.stream()
                .parallel()
                .forEach(Thread::interrupt);

        // Close the server
        serverConnection.close();

        try { onStop(); }
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
        }
    }

    /**
     * @return the UDP connection used by the server
     */
    public BialettiUDPServerConnection getConnection() { return serverConnection; }

    /**
     * @return a {@link BialettiUDPServerConnection} instance
     */
    private BialettiUDPServerConnection openServerSocket() {
        BialettiUDPServerConnection nConnection;
        try { nConnection = new BialettiUDPServerConnection(getPort(), 512); }
        catch (Exception e) {
            // Call handler method
            exceptionHandler.raise(e, this);
            nConnection = null;
        }

        return nConnection;
    }
}
