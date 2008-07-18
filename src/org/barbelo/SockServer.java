package org.barbelo;

import javax.microedition.io.*;

public class SockServer extends Server {
    private static final int PORT = 666;

    public SockServer(GPSd gpsd)
    {
	super(gpsd);
    }

    StreamConnectionNotifier init() throws Exception
    {
	return (StreamConnectionNotifier) Connector.open("socket://:" + PORT);
    }
}
