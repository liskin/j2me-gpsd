package org.barbelo;

import javax.microedition.io.*;
import javax.bluetooth.*;

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

	StreamConnectionNotifier s =
	    (StreamConnectionNotifier) Connector.open(url.toString());

	ServiceRecord sr = localDevice.getRecord(s);

	/* Put it into public browse group */
	DataElement dBrowseGroupList = new DataElement(DataElement.DATSEQ);
	dBrowseGroupList.addElement(new DataElement(DataElement.UUID,
		    new UUID(0x1002 /* PublicBrowseGroup */)));
	sr.setAttributeValue(0x0005 /* BrowseGroupList */, dBrowseGroupList);

	/* Set the Positioning service class bit */
	sr.setDeviceServiceClasses(0x10000 /* Positioning */);

	localDevice.updateRecord(sr);

	return s;
    }
}
