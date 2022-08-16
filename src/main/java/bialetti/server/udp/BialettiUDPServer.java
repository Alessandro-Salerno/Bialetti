package bialetti.server.udp;

import bialetti.annotations.BialettiHandleMethod;
import bialetti.connection.udp.BialettiUDPServerConnection;
import bialetti.server.BialettiServer;
import bialetti.server.BialettiServerExceptionHandler;
import bialetti.util.MethodThread;
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
    private final List<MethodThread> threads;
    /**
     * The thread that keeps the server alive
     */
    private Thread dummyThread;

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
     * Constructor
     * @param port the port
     */
    public BialettiUDPServer(int port) {
        this(port, new BialettiServerExceptionHandler() {
            @Override
            public void onThrowable(Throwable throwable) {
                super.onThrowable(throwable);
            }
        });
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
            MethodThread newThread
                    = new MethodThread(e -> { exceptionHandler.raise(e,
                                                                     BialettiUDPServer.this); },
                                       BialettiUDPServer.this,
                                       method) {
                @Override
                public void run() {
                    try { startThread.join(); }
                    catch (InterruptedException ignored) { }

                    super.run();
                }
            };

            // Start the thread and add it to the list of threads
            newThread.start();
            threads.add(newThread);
        });

        // Create a dummy thread to keep thee server alive
        dummyThread = new Thread(() -> { while (!Thread.interrupted()) { } });
        dummyThread.start();

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
        dummyThread.interrupt();
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
