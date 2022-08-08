package alessandrosalerno.bialetti.server;

/*
 * The standard interface for a BialettiServerEventHandler
 * @author Alessandro-Salerno
 */
public interface BialettiServerEventHandler {
    /*
     * What happens when the server is first started
     * @param server The target server
     */
    void onStart(BialettiServer server) throws Exception;
    /*
     * What happens when the server is stopped
     * @param server The target server
     */
    void onStop(BialettiServer server) throws Exception;
}
