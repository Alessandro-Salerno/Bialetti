package bialetti.server;

import bialetti.BialettiConnection;

/*
 * A representation of a client
 * @author Alessandro-Salerno
 */
public abstract class BialettiServerClientRepresentation {
    /*
     * The connection on which che client runs on
     */
    public final BialettiConnection connection;

    /*
     * Default constructor
     * @param bialettiConnection The connectino
     */
    public BialettiServerClientRepresentation(BialettiConnection bialettiConnection) {
        connection = bialettiConnection;
    }
}
