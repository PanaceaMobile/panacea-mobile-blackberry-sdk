package com.panaceamobile.panacea.sdk;

import java.util.Enumeration;
import java.util.Hashtable;

import net.rim.blackberry.api.browser.URLEncodedPostData;

/**
 * Used to pass input parameters to Panacea Web Calls
 * 
 * @author Cobi Interactive
 **/
public class PMParams
{
	/* Key/Value Pairs */
	private Hashtable keysValues = new Hashtable();

	public PMParams()
	{
	}

	public void put(String key, Object value)
	{
		if (key == null || value == null)
			return;
		keysValues.put(key, value);
	}

	public String get(String key)
	{
		if (key == null)
			return null;

		return keysValues.get(key).toString();
	}


	/**
	 * Constructs the keys and values as URL parameters
	 * 
	 * @return URL encoded parameter String
	 */
	public String getURLParameters()
	{
		
		Enumeration keys = keysValues.keys();
		URLEncodedPostData urlEncoder = new URLEncodedPostData("UTF-8", false);
		while(keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			Object value = keysValues.get(key);
			if(value!=null)
				urlEncoder.append(key.toString(), value.toString());
		}
			
		
		return '&'+urlEncoder.toString();
	}

}
