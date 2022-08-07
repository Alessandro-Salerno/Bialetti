package alessandrosalerno.bialetti.server;

import alessandrosalerno.bialetti.BialettiConnection;
import alessandrosalerno.bialetti.client.BialettiClient;

/*
 * The standard interface for a BialettiClientHandler
 * @author Alessandro-Salerno
 */
public interface BialettiClientHandler {
    /*
     * What happens when the client connects
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the target server
     */
    void onConnect(BialettiConnection client, BialettiServer server);
    /*
     * The main handle method
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the target server
     */
    void handle(BialettiConnection client, BialettiServer server);
    /*
     * What happens when the client disconnects
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the target server
     */
    void onClose(BialettiConnection client, BialettiServer server);
}
