package org.barbelo;

import javax.microedition.io.*;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;

public class BTServer extends Server {
    private static final UUID GPS_SERVER_UUID =
	new UUID("94262f12545811dd923c001a730654bb", false);

    public BTServer(GPSd gpsd)
    {
	super(gpsd);
    }

    StreamConnectionNotifier init() throws Exception
    {
	LocalDevice localDevice = LocalDevice.getLocalDevice();

	StringBuffer url = new StringBuffer("btspp://");
	url.append("localhost").append(':');
	url.append(GPS_SERVER_UUID.toString());
	url.append(";name=NMEA-GPS");
	url.append(";authorize=false");

	return (StreamConnectionNotifier) Connector.open(url.toString());
    }
}
