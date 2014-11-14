package com.panaceamobile.panacea.sdk.messaging;

import java.util.Hashtable;
import java.util.Vector;


public class PMLocalBroadcastManager {
	private static Vector receivers = new Vector();
	
	public static void registerReceiver(PMLocalBroadcastReceiver receiver)
	{
		receivers.addElement(receiver);
	}
	
	public static void unregisterReceiver(PMLocalBroadcastReceiver receiver)
	{
		receivers.removeElement(receiver);
	}
	
	public static void sendBroadcast(Hashtable intent)
	{

		for(int i = 0;i<receivers.size();i++)
			((PMLocalBroadcastReceiver)receivers.elementAt(i)).onReceive(intent);
	}

}
