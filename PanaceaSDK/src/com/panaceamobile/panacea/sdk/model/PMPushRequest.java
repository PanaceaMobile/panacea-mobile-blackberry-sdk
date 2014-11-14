package com.panaceamobile.panacea.sdk.model;

import java.util.Enumeration;


import org.json.me2.JSONObject;

public class PMPushRequest implements PMPushMessage {
	private String request;
	private String message;

	private String device_id;
	
	private String count;
	
	private String sound;
	
	private String seq;


	public PMPushRequest ()
	{
	}

	public PMPushRequest(String JSONStr) throws Exception
	{
		this(new JSONObject(JSONStr));
	}

	public PMPushRequest(JSONObject JSONObj) throws Exception
	{

		parseJsonObject(JSONObj);
	}



	public String toString()
	{
		return new String("{\"request\": \"" + request + "\" "+
				(message!=null? ",\"message\": \""+ message+"\"":"") +
				(device_id!=null? ",\"device_id\": \""+ device_id+"\"":"") +
				(count!=null? ",\"count\": \""+ count+"\"":"") +
				(sound!=null? ",\"sound\": "+ sound+"\"":"") +
				(seq!=null? ",\"seq\": "+ seq+"\"":"") + "}");
	}


	protected void parseJsonObject(JSONObject JSONObj) throws Exception
	{
		String key;
		Enumeration keys = JSONObj.keys();
		while (keys.hasMoreElements())
		{
			key = keys.nextElement().toString();
			if ("request".equals(key))
			{
				request = JSONObj.getString(key);
			}
			else if ("message".equals(key))
			{
				message = JSONObj.getString(key);
			}
			else if ("device_id".equals(key))
			{
				device_id = JSONObj.getString(key);
			}
			else if ("count".equals(key))
			{
				count = JSONObj.getString(key);
			}
			else if("sound".equals(key))
			{
				sound = JSONObj.getString(key);
			}
			else if("seq".equals(key))
			{
				seq = JSONObj.getString(key);
			}
		}
	}

	public String getRequest()
	{
		return request;
	}

	public String getMessage()
	{
		return message;
	}


	public String getDeviceId()
	{
		return device_id;
	}

	public String getCount()
	{
		return count;
	}

	public String getSound()
	{
		return sound;
	}

	public String getSeq()
	{
		return seq;
	}
	
	public void setRequest(String request)
	{
		this.request = request;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public void setDeviceId(String deviceId)
	{
		this.device_id = deviceId;
	}
	
	public void setCount(String count)
	{
		this.count = count;
	}
	
	public void setSound(String sound)
	{
		this.sound = sound;
	}
	
	public void setSeq(String seq)
	{
		this.seq = seq;
	}
	

}
