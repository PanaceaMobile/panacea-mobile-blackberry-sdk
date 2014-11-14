package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class UpdatePreferencesObserver implements Observer {
	
	String channel_key;
	String given_name;
	public UpdatePreferencesObserver(String channel_key, String given_name)
	{
		this.channel_key = channel_key;
		this.given_name = given_name;
	}
	
	
	public void postCompleted(String data) {
		PMBaseResponse response;
		try {
			System.out.println("Update Channel Key returns result:"+ data);
			response = new PMBaseResponse(data);
		

			//Generic response - these variables will be changed for the specific responses
			String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
			int status = response.getStatus();
			String message = response.getDescription();
	
			//save push notification key
			if (response.getStatus() == PMConstants.Status.OK)
			{
				//if push notification key was included
				if (channel_key != null)
				{
					PMPreferencesHelper.setPushNotificationKey(channel_key);
					
					//registration good go to inbox
					result = PanaceaSDK.Result.PUSH_KEY_UPDATED;
				}
				if(given_name!=null)
				{
					PMPreferencesHelper.setGivenName(given_name);
					result = PanaceaSDK.Result.GIVEN_NAME_UPDATED;
				}
				
				//Send successful broadcast
				PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.REGISTER, result,
					status, message,PMWebServiceController.IntentAction.WebService.DEVICE_UPDATE_PREFERENCES);

				System.out.println("Update Preferences success - Result:"+ result + " Message:");
			}

			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataFailure(Tag.REGISTER, result,
				status, message,PMWebServiceController.IntentAction.WebService.DEVICE_UPDATE_PREFERENCES);

			System.out.println("Update Preferences failure - Result:"+ result + " Message:" + message);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void postError(Exception e) {
		// TODO Auto-generated method stub

	}

}
