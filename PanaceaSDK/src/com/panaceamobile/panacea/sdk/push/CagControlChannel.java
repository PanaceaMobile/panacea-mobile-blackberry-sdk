package com.panaceamobile.panacea.sdk.push;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

import org.json.me2.JSONObject;
import org.json.me2.JSONWriter;

import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMPushMessage;
import com.panaceamobile.panacea.sdk.model.PMPushRequest;
import com.panaceamobile.panacea.sdk.model.PMPushResponse;
import com.panaceamobile.panacea.sdk.web.ConnectionManager;

import net.rim.device.api.io.SocketConnectionEnhanced;
import net.rim.device.api.system.DeviceInfo;


/**
 * class CagControlChannel
 * 
 * @author Mike Madsen
 *
 * Implementation of the socket-level CAG Control Channel 
 *
 */
public class CagControlChannel {

	private static final String TAG = "CagControlChannel";
	
	private static final int LOGIN_HEADER_VERSION 			= 1;
	private static final int LOGIN_HEADER_TYPE 				= 1;
	private static final int SOCKET_WRITE_POLL_INTERVAL 	= 100;
	private static final int SOCKET_READ_POLL_INTERVAL 		= 1000;
	private static final int K_MAX_CONNECTION_RETRIES 		= 3;
	private static final int K_WAIT_CONNECTION_RETRY 		= 10000; // 10 secs
	private static final int K_WAIT_CONNECTION_TEST_IS_CONNECTED = 30000; // 30 secs
	private static final int K_WAIT_READ_LOGIN_RESPONSE 	= 5000; // 1 sec
	private static final int K_ERROR_BLACKLISTED 			= 599; // TODO: correct error code for blacklist needed here
	private static final int MAX_LOGIN_RESPONSE_LENGTH		= 10240; // 10k
	
	
	private static final int PUSH_SERVER_PRIMARY_PORT = 5223;
	private static final int PUSH_SERVER_BACKUP_PORT = 443;
	
	private CagControlChannelObserver mObserver = null;
	private StreamConnection mSocket = null;
	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	private volatile Thread mInputThread = null;
	private volatile Thread mOutputThread = null;
	private volatile Thread mConnectionThread = null;
	private Vector mSendQueue = null;
	private int mConnectRetries = 0;
	
	public CagControlChannel(CagControlChannelObserver observer) {
		mObserver = observer;
		mSendQueue = new Vector();
	}
	
	private String getCsAddress() throws Exception {

		String address = "socket://" + PMPreferencesHelper.getPushServerUrl() + ":" + PUSH_SERVER_PRIMARY_PORT; 
	
//    	if ( DeviceInfo.isSimulator() ) {
//            address = address.concat( ";ConnectionTimeout=60000;deviceside=true" );
//    	}
		return address;
		
//		return "socket://127.0.0.1:25";
	}
	
	public void openSocket() {
		CagLog.d(TAG, "openSocket()");
		
		// start connection thread
		mConnectRetries = 0;
		if (mConnectionThread == null) {
			mConnectionThread = new Thread(new Runnable() {
	    		public void run() {
	    			mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_SOCKET_CONNECTING, null, null);
	    			handleSocketConnection();
	    		}
	    	});
			mConnectionThread.setPriority(Thread.NORM_PRIORITY+1);
			mConnectionThread.start();
			CagLog.d(TAG, "openSocket() threadid = "+mConnectionThread.getName());
		}
	}
	
	private void wait(int duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
		}	
	}
	
	private void handleSocketConnection() {
		CagLog.d(TAG, "handleSocketConnection()");
		
		Thread thisThread = Thread.currentThread();
		while (mConnectionThread == thisThread) {
			if (mSocket == null /*|| !mSocket.isConnected()*/) {
				Exception error = null;
				// socket not connected, try connect and login
				while (mConnectionThread == thisThread && mConnectRetries < K_MAX_CONNECTION_RETRIES) {
					try {
						CagLog.d(TAG, "handleSocketConnection() opening socket "+mConnectRetries+" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
						openSocketAndInitialiseQueues();
						performLogin();
						mConnectRetries = 0;
						break;
					} catch (Exception e) {
						CagLog.e(TAG, "handleSocketConnection() failed open socket", e);
						mConnectRetries++;
						error = e;
						wait(K_WAIT_CONNECTION_RETRY);
					}
				}
				
				if (mSocket == null || /*!mSocket.isConnected() ||*/ mInputThread == null || mOutputThread == null) {
					CagLog.e(TAG, "handleSocketConnection() connection retries fail!");
					// failed to login/connect and no more retries  
					closeSocket(true);
					
					// notify session that the connection could not be established
					mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_SOCKET_CONNECT_FAIL, null, error);
					
					// stop connection thread - session handles reconnection attempt
					mConnectionThread = null;
				}
			} else {
				wait(K_WAIT_CONNECTION_TEST_IS_CONNECTED);
				if (mSocket == null /*|| !mSocket.isConnected()*/) {
					CagLog.e(TAG, "handleSocketConnection() remote socket closed");
					// close
					closeSocket(true); // sends event to session
				}
			}
		}	
		
		CagLog.d(TAG, "handleSocketConnection() CLOSED!");
	}
	
	public void openSocketAndInitialiseQueues() throws Exception {
		CagLog.d(TAG, "openSocketAndInitialiseQueues()");
		CagLog.d(TAG, ((mSocket==null)?"socket null":"socket exists!"));

		// getCsAddress will format the url for SSL / non-SSL connections
		mSocket = (StreamConnection) ConnectionManager.getInstance().open(getCsAddress(), Connector.READ_WRITE, true);
		
		SocketConnection sc = (SocketConnection) mSocket;
		sc.setSocketOption(SocketConnection.KEEPALIVE, 1);

		//SocketConnectionEnhanced scext = (SocketConnectionEnhanced) mSocket;
		//scext.setSocketOptionEx( SocketConnectionEnhanced.READ_TIMEOUT, 20000L ); 
		
		// TODO set timeouts somehow for BB !!!
		// set the read timeout
//		int readTimeout = Integer.parseInt(mConfig.getApplicationsSettings().getValue("ReceiveTimeout"));
//		mSocket.setSoTimeout(readTimeout);

		// set the write timeout 
//		int writeTimeout = Integer.parseInt(mConfig.getApplicationsSettings().getValue("SendTimeout"));
//		mSocket.setTimeout(writeTimeout);
	
	 	mInputStream =  mSocket.openInputStream();
		mOutputStream = mSocket.openOutputStream();
		
		mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_SOCKET_CONNECTED, null, null);
		CagLog.d(TAG, "openSocket - done");
	}		
	
	public void closeSocket(boolean reopen) {
		CagLog.d(TAG, "closeSocket()");
		
		resetSendQueue();
		try {
			if (mInputStream != null)
				mInputStream.close();
			if (mOutputStream != null)
				mOutputStream.close();
			if (mSocket != null)
				mSocket.close();
		} catch (IOException e) {
			CagLog.e(TAG, "Error when attempting to close CC socket ", e);
		}
		mInputStream = null;
		mOutputStream = null;
		mSocket = null;
		mInputThread = null;
		mOutputThread = null;
		if (!reopen)
			mConnectionThread = null;
		
		mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_SOCKET_CLOSED, null, null);		
	}
	
	private int getUtcOffset() {
		TimeZone tz = TimeZone.getDefault();
		int offset = tz.getRawOffset() / (1000*60*60);
		return offset;
	}

	
	private void processLoginResponse(PMPushResponse response) throws Exception {
		
		if(response.getStatus()>=0)
		{
			if(response.getKey()!=null)
			{
				
				CagLog.d(TAG, "Login success: CC open");
			    mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_LOGIN_SUCCESS,response.getKey(), null);
			    initialiseMessageQueues();	    	
			} 
			else 
			{
	    		CagLog.e(TAG, "Login response error : no devicekey");
				Exception e = new Exception("Missing devicekey");
				mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_ERROR, null, e);
				throw e;
			}
		}
	}

//	private void updateDeviceUuid() {
//    	CagDevice device = CagPlatform.getInstance().getCagDevice();
//    	
//    	CagConfig config = CagConfig.getInstance();
//    	config.setDeviceUuid(device.getUniqueIdentifier());
//    	config.save();
//    	
//		CagLog.d(TAG, "DeviceId updated: " + config.getDeviceUuid());
//	}
	
//	private void updateProfileUid(String profileUid) {
//		if (profileUid != null) {
//			CagConfig config = CagConfig.getInstance();
//			if (!profileUid.equals(config.getProfileUid())) {
//				// profile UID returned from login does not match. CS has moved this device to another profile. Save settings.
//				config.setProfileUid(profileUid);
//				config.save();
//				CagLog.d(TAG, "ProfileUID updated: "+profileUid);
//			}
//		}
//	}
//	
	private void handleIncomingData() {
		Thread thisThread = Thread.currentThread();
		
		while (mInputThread == thisThread) {
			try {
				
				String message ="";
								
		
				if (mInputStream.available() > 0) {
					//sleep to make sure the rest of the message makes it through
					Thread.sleep(500);
					byte[] buffer = new byte[mInputStream.available()];
					mInputStream.read(buffer);
					message = new String(buffer);
						CagLog.d(TAG, "socket read: "+message);
			
					
					
					if(message.indexOf("request")<0 && message.indexOf("Request")<0)
						receiveMessage(new PMPushResponse(message));
					else
						receiveMessage(new PMPushRequest(message));
				}
			} catch (IOException e) {				
				CagLog.e(TAG, "handleIncomingData() error reading data on CC", e);
				// "TCP receive timed out"
				mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_ERROR, null, e);
			} catch (Exception e) {
				CagLog.e(TAG, "PMPushRequest could not be created from the incomming data");
				mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_ERROR, null, e);
			}
			
			try {
				Thread.sleep(SOCKET_READ_POLL_INTERVAL);
			} catch (InterruptedException e) {
			}
		}
	}

	
	
	private void handleOutgoingData() {
		Thread thisThread = Thread.currentThread();
		while (mOutputThread == thisThread) {
			PMPushMessage msg = null;
			synchronized(mSendQueue) {
				if (mSendQueue.size() > 0) {
					msg = (PMPushMessage) mSendQueue.elementAt(0);
					mSendQueue.removeElementAt(0);
				}
			}

			try {
				if (msg != null) {
					mOutputStream.write(msg.toString().getBytes("UTF-8"),0,msg.toString().getBytes("UTF-8").length);
		        	mOutputStream.flush();
				}
			} catch (Exception e) {
				CagLog.e(TAG, "handleOutgoingData() error writing data on CC", e);
				mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_ERROR, null, e);
			}

			try {
				Thread.sleep(SOCKET_WRITE_POLL_INTERVAL);
			} catch (InterruptedException e) {
			}
		}
	}

	private void initialiseMessageQueues() {
		CagLog.d(TAG, "initialiseMessageQueues()");
		
		// start input thread handing all input from socket
		if (mInputThread == null) {
			mInputThread = new Thread(new Runnable() {
	    		public void run() {
	    			handleIncomingData();
	    		}
	    	});
			mInputThread.setPriority(Thread.NORM_PRIORITY+1);
			mInputThread.start();
		}

		// start output thread handling all data sent on the socket
		if (mOutputThread == null) {
			mOutputThread = new Thread(new Runnable() {
	    		public void run() {
	    			handleOutgoingData();
	    		}
	    	});
			mOutputThread.setPriority(Thread.NORM_PRIORITY+1);
			mOutputThread.start();
		}
	}

	private void performLogin() throws Exception {

		CagLog.e(TAG, "Starting login");
		PMPushRequest loginRequest = new PMPushRequest();
		loginRequest.setRequest("login");
		loginRequest.setDeviceId(PMPreferencesHelper.getUniqueDeviceId());
		try
        {
			
			String test = loginRequest.toString();
			String test2 = test.substring(0,test.length());
			
//        	OutputStreamWriter w = new OutputStreamWriter(mOutputStream);
        	
        	mOutputStream.write(loginRequest.toString().getBytes("UTF-8"),0,loginRequest.toString().getBytes("UTF-8").length);
        	mOutputStream.flush();
   			
        }
        catch(Exception e)
        {
        	//TODO: add some code here for when the login thing fails
        }
		
		

		// wait a bit
		wait(K_WAIT_READ_LOGIN_RESPONSE);
		
		// handle response
		PMPushResponse loginResponseMessage = new PMPushResponse();
        DataInputStream dataInputStream = new DataInputStream(mInputStream);

        // read length
        String response="";
        try {
	        
	        // read payload
	        byte[] buffer = new byte[dataInputStream.available()];
	        dataInputStream.readFully(buffer);
	        CagLog.d(TAG, "login response remaining bytes = "+dataInputStream.available());
	       
	       
	       response = new String(buffer);
        } catch (IOException e) {
        	CagLog.e(TAG, "error reading login reply "+e.getMessage());
        	throw e;
        }

        // process login response (or error)
        loginResponseMessage = new PMPushResponse(response);
        processLoginResponse(loginResponseMessage);    			
	}
	
	/**
	 * sendMessage()
	 * Sends a message to the CS via the control channel
	 * @param message
	 */
	public void sendMessage(PMPushMessage message) {
		CagLog.d(TAG, "sendMessage()");
		if (message != null) {
			synchronized(mSendQueue) {
				mSendQueue.addElement(message);
			}
		}
	}

	/**
	 * receiveMessage
	 * Receives a message sent from the CS to the CAG via the control channel
	 * @param message
	 */
	public void receiveMessage(PMPushMessage message) {
		if (message != null) {
			try {
				mObserver.handleControlChannelEvent(CagControlChannelObserver.CC_EVENT_MESSAGE, message, null);
			} catch (Exception e) {
				CagLog.e(TAG, "receiveMessage() ", e);
			}
		}
	}

	public void resetSendQueue() {
		synchronized(mSendQueue) {
			mSendQueue.removeAllElements();
		}
	}
	
}
