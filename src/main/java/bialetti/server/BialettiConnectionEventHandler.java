package bialetti.server;

/*
 * The standard interface for a BialettiConnectionEventHandler
 * @author Alessandro-Salerno
 */
public interface BialettiConnectionEventHandler<T> {
    /*
     * What happens when the client connects
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the target server
     */
    void onConnect(T client, BialettiServer<T> server) throws Exception;
    /*
     * The main handle method
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the target server
     */
    void handle(T client, BialettiServer<T> server) throws Exception;
    /*
     * What happens when the client disconnects
     * @param client The BialettiConnection instance of the target client
     * @param server The BialettiServer instance of the target server
     */
    void onClose(T client, BialettiServer<T> server);
}
