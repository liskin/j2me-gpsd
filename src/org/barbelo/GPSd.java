package org.barbelo;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Vector;

public class GPSd extends MIDlet implements CommandListener {
	protected GUIStack	_gs;
	private Form		_form;
	private GPS		_gps;
	private Command		_cmdexit;
	private Command		_cmdhide;
	private Command		_cmdsettings;
	private Command		_cmdabout;
	private StringItem	_debug;
	private StringItem	_latitude;
	private StringItem	_longitude;
	private StringItem	_clients;
	private Vector		_servers = new Vector();
	protected Config	_config = new Config();

	public GPSd()
	{
		_form = new Form("GPSd");
		_form.addCommand(_cmdexit = new Command("Exit", Command.EXIT, 0));
		_form.addCommand(_cmdhide = new Command("Hide", Command.SCREEN, 0));
		_form.addCommand(_cmdsettings = new Command("Settings", Command.SCREEN, 0));
		_form.addCommand(_cmdabout = new Command("About", Command.HELP, 0));
		_form.setCommandListener(this);

		_latitude	= new StringItem("Latitude", "");
		_longitude	= new StringItem("Longitude", "");
		_clients	= new StringItem("Clients", "0");
		_debug		= new StringItem("Debug", "");

		_form.append(_latitude);
		_form.append(_longitude);
		_form.append(_clients);
		_form.append(_debug);

		_gps    = new GPS(this);
	}

	public void startApp()
	{
		_config.load();
		_gs = new GUIStack(Display.getDisplay(this), _form);

		_gps.start();
		start_servers();
	}

	public void pauseApp()
	{
	}

	public void destroyApp(boolean unconditional)
	{
	}

	public void commandAction(Command c, Displayable d)
	{
		if (c == _cmdhide) {
			_gs.hide();
		} else if (c == _cmdexit) {
			_gps.stop();
			stop_servers();
			destroyApp(false);
			notifyDestroyed();
		} else if (c == _cmdsettings) {
			_gs.push(new Settings(this), false);
		} else if (c == _cmdabout) {
			_gs.push(new About());
		}
	}

	public void exception(Exception e)
	{
		Alert alert = new Alert("Error", e.toString(), null, AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		_gs.pushAlert(alert);
	}

	public void debug(String msg)
	{
		_debug.setText(msg);
	}

	public void update_location(double latitude, double longitude,
				    String nmea)
	{
		_latitude.setText((new Double(latitude)).toString());
		_longitude.setText((new Double(longitude)).toString());

		for (int i = 0; i < _servers.size(); i++) {
			Server s = (Server) _servers.elementAt(i);

			s.update_location(nmea);
		}
	}

	public void update_client_count()
	{
		int clients = 0;

		for (int i = 0; i < _servers.size(); i++) {
			Server s = (Server) _servers.elementAt(i);

			clients += s.client_count();
		}

		_clients.setText((new Integer(clients)).toString());
	}

	private void start_servers()
	{
		_servers.addElement(new SockServer(this));
		_servers.addElement(new BTServer(this));

		for (int i = 0; i < _servers.size(); i++) {
			Server s = (Server) _servers.elementAt(i);

			if (!"0".equals(_config.get(s.serverName())))
			    s.start();
		}
	}

	private void stop_servers()
	{
		for (int i = 0; i < _servers.size(); i++) {
			Server s = (Server) _servers.elementAt(i);

			s.stop();
			s.interrupt();
			try {
				s.join();
			} catch (InterruptedException e) {
			}
		}

		_servers.removeAllElements();
	}

	public void reconfigure()
	{
	    stop_servers();
	    start_servers();
	    _gps.start();
	}
}

class About extends Form {
    About() {
	super("About");
	append("GPSd - an application that turns your internal GPS into a NMEA GPS for use with other applications and devices.\n");
	append("This is free software. There is NO warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n");
       	append("For details, help and updates see the homepage:\nhttp://wiki.nomi.cz/gpsd:start");
    }
}
