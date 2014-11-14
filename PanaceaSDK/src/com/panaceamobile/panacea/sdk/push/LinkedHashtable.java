package com.panaceamobile.panacea.sdk.push;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.util.Persistable;

/**
 * LinkedHashtable
 * Maintains order of added items
 * @author Mike
 *
 */
public class LinkedHashtable extends Hashtable implements Persistable {
	
	private PersistableVector vector = new PersistableVector();
	
	public Object put(Object key, Object value) {
		Object ret = super.put(key, value);
		if (ret == null)
			vector.addElement(key);
		return super.put(key, value);
	}
	
	public Object remove(Object key)
	{
		vector.removeElement(key);
		return super.remove(key);
	}
	
	public Enumeration keys() {
		return vector.elements();
	}
	
	public Enumeration elements() {
		Vector ordered = new Vector();
		for (Enumeration e=keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			ordered.addElement(get(key));
		}
		return ordered.elements();
	}
	
	public Object getAt(int index)
	{
		return get(vector.elementAt(index));
	}
	
	public int indexOf(Object key)
	{
		return vector.indexOf(key);
	}
	static class PersistableVector extends Vector implements Persistable
	{
		
	}
}
