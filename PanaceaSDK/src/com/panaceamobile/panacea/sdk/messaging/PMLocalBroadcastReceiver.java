package com.panaceamobile.panacea.sdk.messaging;


import java.util.Hashtable;

import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.webservices.PMWebServiceController;



/**
 * This Local broadcast receiver allows users to register for web service
 * callbacks from {@link PMWebServiceController}.
 * <p>
 * This is done implementing the {@link PMBroadcastListener} and by creating an
 * instance of {@link PMLocalBroadcastReceiver} in the class that should receive
 * callbacks:
 * <p>
 * <code>protected PMLocalBroadcastReceiver mReceiver = new PMLocalBroadcastReceiver();</code>
 * <p>
 * and then registering and unregistering where required:
 * <p>
 * <code>mReceiver.register(this, this);</code>
 * <p>
 * <code>mReceiver.unregister(this);</code>
 * 
 * @author Cobi Interactive
 * @see PMWebServiceController
 */
public class PMLocalBroadcastReceiver
{
	private static final String EVENT_NAME = "com.panacea.PMLocalBroadcastReceiver";

	private static final String EXTRA_TAG = "EXTRA_TAG";
	private static final String EXTRA_TYPE = "EXTRA_TYPE";
	private static final String EXTRA_RESULT = "EXTRA_RESULT";
	private static final String EXTRA_CODE = "EXTRA_CODE";
	private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	private static final int TYPE_PRE_DATA_EXECUTE = 0;
	private static final int TYPE_POST_DATA_FAILURE = 1;
	private static final int TYPE_POST_DATA_SUCCESS = 2;


	/**
	 * This interface defines the various methods received by the broadcast
	 * receiver.
	 * 
	 * @author Cobi Interactive
	 */
	public interface PMBroadcastListener
	{
		public void onPreDataExecute(String tag);

		public void onPostDataSuccess(String tag, String result, int status, String message, String method);

		public void onPostDataFailure(String tag, String result, int httpCode, String message, String method);
	}

	private PMBroadcastListener listener;
	

	public void onReceive(Hashtable intent)
	{
		if (listener != null)
		{
			String tag = (String)intent.get(EXTRA_TAG);
				String message =(String) intent.get(EXTRA_MESSAGE);
			String result =(String) intent.get(EXTRA_RESULT);

			Integer code =(Integer)intent.get(EXTRA_CODE);
			Integer type =(Integer) intent.get(EXTRA_TYPE);
			
			String method = (String)intent.get("Action");

			if(code==null)
				code = new Integer(-1);
			
			if(type==null)
				type = new Integer(-1);
			
			switch (type.intValue())
			{
				case TYPE_PRE_DATA_EXECUTE:
				{
					listener.onPreDataExecute(tag);
				}
					break;
				case TYPE_POST_DATA_FAILURE:
				{
					listener.onPostDataFailure(tag, result, code.intValue(), message, method);
				}
					break;
				case TYPE_POST_DATA_SUCCESS:
				{
					listener.onPostDataSuccess(tag, result, code.intValue(), message,method);
				}
					break;

				default:
					break;
			}
		}
	}

	public void register(PMBroadcastListener listener)
	{
		this.listener = listener;
		PMLocalBroadcastManager.registerReceiver(this);

	}

	public void unregister()
	{
		this.listener = null;
		PMLocalBroadcastManager.unregisterReceiver(this);
	}

	public static void sendPreDataExecute(String tag)
	{
		Hashtable intent = new Hashtable();
		intent.put("Name", EVENT_NAME);
		intent.put(EXTRA_TAG, tag);
		intent.put(EXTRA_TYPE, new Integer(TYPE_PRE_DATA_EXECUTE));

		PMLocalBroadcastManager.sendBroadcast(intent);
	}

	public static void sendPostDataSuccess(String tag, String result, int status,
		String message, String method)
	{
		Hashtable intent = new Hashtable();
		intent.put("Name", EVENT_NAME);
		intent.put("Action", method);
		intent.put(EXTRA_TAG, tag);
		intent.put(EXTRA_TYPE, new Integer(TYPE_POST_DATA_SUCCESS));
		intent.put(EXTRA_RESULT, result);
		intent.put(EXTRA_CODE, new Integer(status));
		intent.put(EXTRA_MESSAGE, message);

		PMLocalBroadcastManager.sendBroadcast(intent);
	}

	public static void sendPostDataFailure(String tag, String result, int code,
		String message, String method)
	{
		Hashtable intent = new Hashtable();
		intent.put("Name",EVENT_NAME);

		intent.put("Action", method);
		intent.put(EXTRA_TAG, tag);
		intent.put(EXTRA_TYPE, new Integer(TYPE_POST_DATA_FAILURE));
		intent.put(EXTRA_RESULT, result);
		intent.put(EXTRA_CODE, new Integer(code));
		intent.put(EXTRA_MESSAGE, message);

		PMLocalBroadcastManager.sendBroadcast(intent);
	}
}
