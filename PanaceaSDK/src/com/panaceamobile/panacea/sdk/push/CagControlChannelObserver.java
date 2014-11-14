package com.panaceamobile.panacea.sdk.push;

/**
 * class CagControlChannelObserver
 * 
 * Observer interface for the control channel
 * 
 * @author Mike Madsen
 *
 */
public interface CagControlChannelObserver {

	/**
	 * Control channel event constants
	 */
	public static final int CC_EVENT_NONE 					= 1000;
	public static final int CC_EVENT_SOCKET_CONNECTING		= 1001;
	public static final int CC_EVENT_SOCKET_CONNECTED		= 1002;
	public static final int CC_EVENT_LOGGING_IN	 			= 1003;
	public static final int CC_EVENT_LOGIN_SUCCESS 			= 1004;
	public static final int CC_EVENT_LOGIN_FAIL 			= 1005;
	public static final int CC_EVENT_ERROR 					= 1006;
	public static final int CC_EVENT_SOCKET_CLOSED			= 1007;
	public static final int CC_EVENT_SOCKET_CONNECT_FAIL	= 1008;
	public static final int CC_EVENT_MESSAGE				= 1009;
	
	/**
	 * handleControlChannelEvent() handle a control channel event
	 * @param eventId one of the CagControlChannelObserver event ids
	 * @param arg1 message (or null)
	 * @param arg2 error (or null)
	 */
	public void handleControlChannelEvent(int eventId, Object arg1, Object arg2);
}
