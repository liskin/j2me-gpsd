package org.barbelo;

import javax.microedition.io.*;
import java.io.*;

public class ClientHandler {
	private StreamConnection	_s;
	private DataOutputStream	_os;

	public ClientHandler(StreamConnection s) throws Exception
	{
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

	public synchronized void update_location(String nmea) throws Exception
	{
		byte[] crap = nmea.getBytes("UTF-8");
		_os.write(crap);
		_os.flush();
	}
}
