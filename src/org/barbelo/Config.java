package org.barbelo;

import javax.microedition.rms.*;
import java.util.*;

/**
 * Permanent configuration storage.
 */
public class Config {
    /**
     * The record store.
     */
    private RecordStore store;
    private Hashtable keys = new Hashtable();

    private void open() throws Exception {
	if (store == null)
	    store = RecordStore.openRecordStore("gpsd_config", true);
    }

    public void close() {
	try {
	    if (store != null) {
		store.closeRecordStore();
		store = null;
	    }
	} catch (Exception e) {}
    }

    /**
     * Load configuration variables from record store.
     */
    public void load() {
	try {
	    open();

	    RecordEnumeration enu = store.enumerateRecords(null, null, false);
	    while (enu.hasNextElement()) {
		int id = enu.nextRecordId();
		byte text[] = store.getRecord(id);

		for (int i = 0; i < text.length; i++)
		    if (text[i] == '=') {
			String key = new String(text, 0, i, "UTF-8");
			String val = new String(text, i + 1, text.length - i - 1, "UTF-8");

			keys.put(key, new Pair(id, val));
			break;
		    }
	    }
	} catch (Exception e) {}
    }

    /**
     * Get a configuration value.
     */
    public String get(String key) {
	Pair p = (Pair) keys.get(key);
	if (p == null)
	    return null;
	else
	    return p.val;
    }

    /**
     * Set a configuration value.
     */
    public void set(String key, String val) {
	try {
	    StringBuffer textb = new StringBuffer(key);
	    textb.append('=');
	    textb.append(val);
	    byte[] text = textb.toString().getBytes("UTF-8");

	    Pair p = (Pair) keys.get(key);
	    if (p == null) {
		int id = store.addRecord(text, 0, text.length);
		keys.put(key, new Pair(id, val));
	    } else {
		store.setRecord(p.id, text, 0, text.length);
		p.val = val;
	    }
	} catch (Exception e) {}
    }
}

class Pair {
    public int id;
    public String val;

    public Pair(int id, String val) {
	this.id = id;
	this.val = val;
    }
}
