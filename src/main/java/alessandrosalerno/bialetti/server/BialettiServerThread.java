package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;
import alessandrosalerno.bialetti.BialettiEventHandler;

import java.io.IOException;

class BialettiServerThread extends Thread {
    private BialettiConnection connectedClient;
    private BialettiServer hostServer;
    private final BialettiEventHandler eventHandler;

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
            System.out.println("Thread closed!");
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
