package bialetti.server;

import bialetti.BialettiConnection;

public abstract class BialettiServerClientRepresentation {
    public final BialettiConnection connection;

    public BialettiServerClientRepresentation(BialettiConnection bialettiConnection) {
        connection = bialettiConnection;
    }
}
