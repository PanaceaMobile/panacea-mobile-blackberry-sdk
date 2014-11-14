package com.panaceamobile.panacea.sdk.model;

import java.util.Enumeration;


import org.json.me2.JSONObject;

public class PMPushResponse implements PMPushMessage{
	private int status;
	private String message;
	private String key;

	public PMPushResponse()
	{
	}

	public PMPushResponse(String JSONStr) throws Exception
	{
		this(new JSONObject(JSONStr));
	}

	public PMPushResponse(JSONObject JSONObj) throws Exception
	{

		parseJsonObject(JSONObj);
	}



	public String toString()
	{
		return new String("{ \"status\": \"" + status + "\"" + (message!=null?", \"message\": \"" + message + "\"}":"}" ));
	}


	protected void parseJsonObject(JSONObject JSONObj) throws Exception
	{
		String key;
		Enumeration keys = JSONObj.keys();
		while (keys.hasMoreElements())
		{
			key = keys.nextElement().toString();
			if ("status".equals(key))
			{
				status = JSONObj.getInt(key);
			}
			else if ("message".equals(key))
			{
				message = JSONObj.getString(key);
			}
			else if("key".equals(key))
			{
				this.key = JSONObj.getString(key);
			}
			
		}
	}

	public int getStatus()
	{
		return status;
	}

	public String getMessage()
	{
		return message;
	}

	public String getKey()
	{
		return this.key;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}

}
