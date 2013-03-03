# J2ME GPS daemon

Author: Tomáš Janoušek <http://work.lisk.in/>, Andrea Bittau <a.bittau@cs.ucl.ac.uk>

This is a modified version of GPSd from
[Barbelo](http://darkircop.org/barbelo/), which turns your phone with internal
GPS into a Bluetooth GPS that can be used from a computer, for example.
Example applications include wardriving using just your phone and notebook,
etc.

![](img/gpsd.png)

## Download
  * <http://store.lisk.in/tmp/perm/GPSd-v1.2.jar>

## Alternatives
  * <http://gagravarr.org/code/#s60_as_bt_gps>
  * <http://www.symarctic.com/beta/static.php?page=extgps_download>

## Linux gpsd howto
  * Install GPSd on the phone and run it.
  * `$ sdptool records <bt address>` or `$ sdptool search SP`
    * (you can get the address by typing `*#2820#` on Symbian phones; or use the second command)
    * You'll get (hopefully) output containing something like this (we are
      interested in the channel number):

            Service Name: NMEA-GPS
            Service RecHandle: 0x1000e
            Service Class ID List:
              UUID 128: 94262f12-5458-11dd-923c-001a730654bb
              "Serial Port" (0x1101)
            Protocol Descriptor List:
              "L2CAP" (0x0100)
              "RFCOMM" (0x0003)
                Channel: 5

  * `$ rfcomm connect rfcomm5 <bt address> <channel number>`
    * Now, `/dev/rfcomm5` is connected and using `cu -l /dev/rfcomm5` one may look at the raw NMEA data.
    * Note that you may need to run this command every time gpsd wants gps.
      Configure it permanently in `/etc/bluetooth/rfcomm.conf`, if you want.
  * Configure your gpsd to use `/dev/rfcomm5`. This was in `/etc/default/gpsd` on my Debian box.
  * Try it.
    * `$ xgps`  
      ![](img/xgps.png)
  * You're done!

## Working devices
Hopefully all devices with Mobile Java, MIDP 2.0,
[JSR-179](http://jcp.org/en/jsr/detail?id=179) (Location API),
[JSR-082](http://jcp.org/en/jsr/detail?id=82) (Bluetooth API) and an internal
GPS (well, Symbian lets you configure location source so internal GPS may not
be required, but what's the point then?).

It has been tested on the following phones (if your phone isn't listed and meets the requirements, it means nothing, it should work anyway):
  * Samsung i560 (satellite view not working due to firmware deficiency)
  * Nokia 5800 ExpressMusic (satellite view working, as with any other Nokia phone)
