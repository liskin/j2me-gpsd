package org.barbelo;

import javax.microedition.io.*;

public class SockServer extends Server {
    public static final int PORT = 666;

    public SockServer(GPSd gpsd)
    {
	super(gpsd);
    }

    public String serverName()
    {
	return "SockServer";
    }

    StreamConnectionNotifier init() throws Exception
    {
	String port = _gpsd._config.get("SockServer_port");
	return (StreamConnectionNotifier) Connector.open("socket://:" +
		(port != null ? port : Integer.toString(PORT)));
    }
}
