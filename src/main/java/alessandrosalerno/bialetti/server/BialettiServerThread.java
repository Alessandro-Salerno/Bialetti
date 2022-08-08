package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;

import java.io.IOException;

/*
 * Class that extends java s standard Thread
 * Used to handle clients
 * @author Alessandro-Salerno
 */
class BialettiServerThread extends Thread {
    /*
     * The target client
     */
    private BialettiConnection connectedClient;
    /*
     * The host server
     */
    private BialettiServer hostServer;
    /*
     * The event handler
     */
    private final BialettiEventHandler eventHandler;

    /*
     * Default constructor
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the host server
     * @param handler The BialettiEventHandler instance for the target server
     */
    public BialettiServerThread(BialettiConnection client, BialettiServer server, BialettiEventHandler handler) {
        connectedClient = client;
        hostServer      = server;
        eventHandler    = handler;
    }

    @Override
    public void run() {
        try {
            // Call initial connection method
            eventHandler.onConnect(connectedClient, hostServer);

            // Main handler loop
            while (!Thread.interrupted()) {
                // Call handler method
                eventHandler.handle(connectedClient, hostServer);
            }
        }

        // Do nothing if an interrupt is triggered
        catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        // Exception handler
        catch (Exception e) {
            try {
                // Close socket connection
                hostServer.closeConnection(connectedClient);
            }

            // Exception handler
            catch (IOException ioException) {
                System.out.println("[-] Unable to close connection");
                ioException.printStackTrace();
            }
        }
    }
}
