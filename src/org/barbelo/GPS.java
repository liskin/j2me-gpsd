package org.barbelo;

import javax.microedition.location.*;

public class GPS implements LocationListener {
	private GPSd			_gpsd;
	private LocationProvider	_lp;
	private double			_latitude;
	private double			_longitude;

	public GPS(GPSd gpsd)
	{
		_gpsd = gpsd;

		try {
			Criteria cr = new Criteria();
			_lp = LocationProvider.getInstance(cr);
		} catch (Exception e) {
			_gpsd.exception(e);
		}

		_latitude = _longitude = 0;
	}

	public void start()
	{
		_lp.setLocationListener(this, 1, 1, 1);

		notify_gpsd();
	}

	public void stop()
	{
		_lp.setLocationListener(null, -1, -1, -1);
	}

	public void locationUpdated(LocationProvider lp, Location location)
	{
		double latitude  = 0;
		double longitude = 0;
		boolean diff;

		if (location.isValid()) {
			Coordinates c = location.getQualifiedCoordinates();

			latitude  = c.getLatitude();
			longitude = c.getLongitude();
		}

		diff = (latitude != _latitude) || (longitude != _longitude);

		_latitude  = latitude;
		_longitude = longitude;

		if (diff)
			notify_gpsd();
	}

	public void providerStateChanged(LocationProvider lp, int newState)
	{
	}

	private void notify_gpsd()
	{
		_gpsd.set_coordinates(_latitude, _longitude);
	}
}
