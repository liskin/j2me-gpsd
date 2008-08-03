package org.barbelo;

import javax.microedition.lcdui.*;

class Settings extends Form implements CommandListener {
    private GPSd gpsd;
    private Item[] it = new Item[4];
    private Command exit;

    private static String onoff[] = { "disabled", "enabled" };

    Settings(GPSd gpsd) {
	super("Settings");
	this.gpsd = gpsd;

	exit = new Command("Back", Command.BACK, 0);
	addCommand(exit);
	setCommandListener(this);

	it[0] = new ChoiceGroup("BT Server", ChoiceGroup.POPUP, onoff, null);
	((ChoiceGroup) it[0]).setSelectedIndex("0".equals(gpsd._config.get("BTServer")) ? 0 : 1, true);

	it[1] = new ChoiceGroup("Socket Server", ChoiceGroup.POPUP, onoff, null);
	((ChoiceGroup) it[1]).setSelectedIndex("0".equals(gpsd._config.get("SockServer")) ? 0 : 1, true);

	String SockServer_port = gpsd._config.get("SockServer_port");
	it[2] = new TextField("Socket Server port",
		SockServer_port != null ? SockServer_port : Integer.toString(SockServer.PORT),
		5, TextField.NUMERIC);
	
	String GPS_interval = gpsd._config.get("GPS_interval");
	it[3] = new TextField("GPS update interval", GPS_interval != null ? GPS_interval : "1", 3,
		TextField.NUMERIC);

	for (int i = 0; i < it.length; i++)
	    append(it[i]);
    }

    public void commandAction(Command c, Displayable d)
    {
	if (c == exit) {
	    gpsd._config.set("BTServer", Integer.toString(((ChoiceGroup) it[0]).getSelectedIndex()));
	    gpsd._config.set("SockServer", Integer.toString(((ChoiceGroup) it[1]).getSelectedIndex()));
	    gpsd._config.set("SockServer_port", ((TextField) it[2]).getString());
	    gpsd._config.set("GPS_interval", ((TextField) it[3]).getString());

	    gpsd._gs.pop();
	    gpsd.reconfigure();
	}
    }
}
