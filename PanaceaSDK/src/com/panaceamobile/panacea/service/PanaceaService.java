package com.panaceamobile.panacea.service;

import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Result;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver.PMBroadcastListener;
import com.panaceamobile.panacea.sdk.model.PMPushMessage;
import com.panaceamobile.panacea.sdk.model.PMPushRequest;
import com.panaceamobile.panacea.sdk.model.PMPushResponse;
import com.panaceamobile.panacea.sdk.push.CagControlChannel;
import com.panaceamobile.panacea.sdk.push.CagControlChannelObserver;
import com.panaceamobile.panacea.sdk.push.Log;
import com.panaceamobile.panacea.sdk.web.HttpPost;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.CodeModuleManager;
public class PanaceaService implements  CagControlChannelObserver
{
	public PanaceaService()
	{
		super();
		pushChannel = new CagControlChannel(this);
		
	
	}
	private CagControlChannel pushChannel;
	private PMLocalBroadcastReceiver receiver;
	private static final int POLL_INTERVAL = 15000;
	private static final int PING_TIMEOUT = 10000;
	private static final String URL_OF_WEBSERVICE_USED_TO_GET_PUSH_CHANNEL_URL = "http://blackberry.panaceamobile.com/getserver";
	private boolean startup = true;
	private boolean loggedIntoChannel = false;
	
	
	private boolean waitingForPing = false;
	
	public String getPushChannelUrlRequestUrl()
	{
		return URL_OF_WEBSERVICE_USED_TO_GET_PUSH_CHANNEL_URL + "?device_id=" + PMPreferencesHelper.getUniqueDeviceId();
	}
	
	public void initPushChannel()
	{
		
		HttpPost.post(getPushChannelUrlRequestUrl(), null, "application/json", new Observer()
		{

			public void postCompleted(String data) 
			{
				PMPreferencesHelper.setPushServerUrl(data);
				pushChannel.openSocket();
				loggedIntoChannel = true;
				
			}

			public void postError(Exception e) 
			{
				System.out.println(e + " "  + e.getMessage());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					System.out.println(e1 + " "  + e1.getMessage());
				}
				initPushChannel();
			}
		
		});
		
	}

	public void initPermissions() {
        // Check app permissions
        int [] reqp = { 
        		ApplicationPermissions.PERMISSION_DEVICE_SETTINGS,
        		ApplicationPermissions.PERMISSION_FILE_API,
        		ApplicationPermissions.PERMISSION_IDLE_TIMER,
        		ApplicationPermissions.PERMISSION_INTERNET,
        		ApplicationPermissions.PERMISSION_LOCATION_DATA,
        		ApplicationPermissions.PERMISSION_MEDIA,
        		ApplicationPermissions.PERMISSION_ORGANIZER_DATA,
        		ApplicationPermissions.PERMISSION_PHONE,
        		ApplicationPermissions.PERMISSION_RECORDING,
        		ApplicationPermissions.PERMISSION_SERVER_NETWORK,
        		ApplicationPermissions.PERMISSION_USB,
        		ApplicationPermissions.PERMISSION_WIFI,
        		ApplicationPermissions.PERMISSION_CROSS_APPLICATION_COMMUNICATION

        };
        
        ApplicationPermissionsManager apm = ApplicationPermissionsManager.getInstance(); 
        ApplicationPermissions perms = apm.getApplicationPermissions();
        
        ApplicationPermissions toRequest = new ApplicationPermissions();
        for (int i=0; i<reqp.length; i++) {
        	if (!perms.containsPermissionKey(reqp[i])) {
        		toRequest.addPermission(reqp[i]);
        	} 
        	else if (perms.getPermission(reqp[i]) != ApplicationPermissions.VALUE_ALLOW) {
        			toRequest.addPermission(reqp[i]);
        	}        	
        }
        
        if (toRequest.getPermissionKeys().length > 0) {        	
        	apm.invokePermissionsRequest(toRequest);
        }	        
	}
	

	
	public void run() 
	{
	
		while (true) 
		{
			try 
			{
				
				
				if (startup)
				{
					startup = false;
					
				}
				else if(loggedIntoChannel)
				{
					waitingForPing = true;
					PMPushRequest ping = new PMPushRequest();
					ping.setRequest("ping");
					pushChannel.sendMessage(ping);
					Thread.sleep(PING_TIMEOUT);
					if(waitingForPing)
					{
						//TODO: if the ping times out what do we do?
						restartSocketConnection();
					}
				}
				
				Thread.sleep(POLL_INTERVAL);
				
				
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
		}
	}

	
	

	public void handleControlChannelEvent(int eventId, Object arg1, Object arg2) {
		
		switch (eventId) {
		case CC_EVENT_LOGIN_SUCCESS: 
		
			if (arg1 instanceof String) 
			{
				String deviceKey = (String) arg1;
				PMPreferencesHelper.setPushNotificationKey(deviceKey);
				loggedIntoChannel = true;
			}				
			break;		
		case CC_EVENT_LOGIN_FAIL:
			//TODO: perhaps do a retry?
			loggedIntoChannel = false;
			break;
		case CC_EVENT_ERROR:
			pushChannel.closeSocket(false);
			break;
		case CC_EVENT_LOGGING_IN:
		
			break;
		case CC_EVENT_SOCKET_CONNECTING:
			
			break;
		case CC_EVENT_SOCKET_CONNECTED:
			
			break;
		case CC_EVENT_SOCKET_CLOSED:
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				System.out.println("Sleep before restarting socket was interrupted");
			}
			
			pushChannel.openSocket();
			break;
		case CC_EVENT_SOCKET_CONNECT_FAIL:
			
			break;
		case CC_EVENT_MESSAGE:
			if (arg1 instanceof PMPushMessage)
				handlePushChannelMessage((PMPushMessage) arg1);
			break;
					
		default:
			break;
		}
	}
	
	private void handlePushChannelMessage(PMPushMessage message)
	{
		if(message!=null)
		{
			this.waitingForPing = false;
			if(message instanceof PMPushRequest)
			{
				PMPushRequest request = (PMPushRequest)message;
				
				if(request.getRequest().equals("ping") || request.getRequest().equals("Ping"))
				{
					PMPushResponse pingback = new PMPushResponse();
					pingback.setStatus(0);
					pingback.setMessage("ping");
					pushChannel.sendMessage(pingback);
				}
				else if(request.getRequest().equals("push")||request.getRequest().equals("Request"))
//				else if(request.getRequest().equals("message")||request.getRequest().equals("Message"))
				{
					PMPushRequest acknowledge = new PMPushRequest();
					acknowledge.setRequest("ack");
					acknowledge.setSeq(request.getSeq());
					pushChannel.sendMessage(acknowledge);
					ApplicationManager am = ApplicationManager.getApplicationManager();
					ApplicationDescriptor[] apps = am.getVisibleApplications();
					String test = PMPreferencesHelper.getApplicationName();
					int test2 = CodeModuleManager.getModuleHandle(PMPreferencesHelper.getApplicationName());
					ApplicationDescriptor[] appsWithModuleName = CodeModuleManager.getApplicationDescriptors(CodeModuleManager.getModuleHandle(PMPreferencesHelper.getApplicationName()));
					
					ApplicationDescriptor app = appsWithModuleName[1];
					boolean appIsRunning = false;
					for(int i=0;i<apps.length;i++)
					{
						ApplicationDescriptor visapp = apps[i];
						if(visapp.getName().equals(app.getName()))
						{
							appIsRunning = true;
							break;
						}
							
					}
					if(!appIsRunning)
					{
					
						
						int currentprocessId = am.getForegroundProcessId();
						
						
						try 
						{
							am.runApplication(app,false);
						} 
						catch (ApplicationManagerException e) 
						{
							System.out.println(e + e.getMessage());
						}
						
						//puts the app in the background
						am.requestForeground(currentprocessId);
					}
					else
					{
						am.postGlobalEvent(PMPreferencesHelper.getEventKey());
					}
					
				}
				
			}
			else if(message instanceof PMPushResponse)
			{
				PMPushResponse response = (PMPushResponse)message;
				
				if(response.getStatus()<0)
				{
						//TODO: Handle some sort of error
					
				}
			
			}
		}
	}
	
	private void restartSocketConnection()
	{
		pushChannel.closeSocket(true);
		
	}

}
