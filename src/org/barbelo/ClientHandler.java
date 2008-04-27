package org.barbelo;

import javax.microedition.io.*;
import java.io.*;

public class ClientHandler {
	private Server			_server;
	private SocketConnection	_s;
	private DataOutputStream	_os;

	private static final int CMD_COORDINATES = 0;

	public ClientHandler(Server server, SocketConnection s) throws Exception
	{
		_server = server;
		_s      = s;
		_os     = _s.openDataOutputStream();
	}

	public synchronized void kill()
	{
		try {
			_os.close();
			_s.close();
			_os = null;
		} catch (Exception e) {
		}
	}

	public synchronized void set_coordinates(double latitude,
						 double longitude)
							throws Exception
	{
		String msg = "" + CMD_COORDINATES 
			     + " " + latitude
			     + " " + longitude
			     + "\n";

		byte[] crap = msg.getBytes("UTF-8");
		_os.write(crap);
		_os.flush();
	}
}
