package org.barbelo;

import javax.microedition.io.*;
import java.util.*;

public abstract class Server extends Thread {
	protected GPSd			_gpsd;
	private boolean			_running = false;
	private StreamConnectionNotifier	_sock = null;
	private Vector			_clients = new Vector();

	abstract public String serverName();

	public Server(GPSd gpsd)
	{
		_gpsd    = gpsd;
	}

	private synchronized boolean running()
	{
		return _running;
	}

	public synchronized void stop()
	{
		_running = false;

		try {
			if (_sock != null) {
				_sock.close();
				_sock = null;
			}
		} catch (Exception e) {
		}

		for (int i = 0; i < _clients.size(); i++) {
			ClientHandler c = (ClientHandler) _clients.elementAt(i);

			c.kill();
		}

		_clients.removeAllElements();
		_gpsd.update_client_count();
	}

	public void update_location(String nmea)
	{
		boolean recount = false;

		for (int i = 0; i < _clients.size(); i++) {
			ClientHandler c = (ClientHandler) _clients.elementAt(i);

			try {
				c.update_location(nmea);
			} catch (Exception e) {
				c.kill();
				_clients.removeElementAt(i);
				i--;
				recount = true;
			}
		}

		if (recount)
			_gpsd.update_client_count();
	}

	abstract StreamConnectionNotifier init() throws Exception;

	public void run()
	{
		_running = true;

		try {
			_sock = init();
		} catch (Exception e) {
			_gpsd.exception(e);
			return;
		}

		while (running()) {
			run_do();
		}
	}

	private void run_do()
	{
		StreamConnection s = null;

		try {
			s = (StreamConnection) _sock.acceptAndOpen();
		} catch (Exception e) {
			return;
		}

		ClientHandler c = null;
		try {
			c = new ClientHandler(s);
		} catch (Exception e) {
			return;
		}

		_clients.addElement(c);
		_gpsd.update_client_count();
	}

	public int client_count()
	{
		return _clients.size();
	}
}
