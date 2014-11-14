package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;
import com.panaceamobile.panacea.sdk.webservices.PMWebServiceController.IntentAction;

public class DeviceCheckStatusObserver implements Observer {

	public void postCompleted(String data) {
		PMBaseResponse response;
		try {
			System.out.println("Device Check Status returns result:"+ data);
			response = new PMBaseResponse(data);


			//Generic response - these variables will be changed for the specific responses
			String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
			int status = response.getStatus();
			String message = response.getDescription();
	
			//save messages device settings after a successful response
			if (response.getStatus() == PMConstants.Status.OK)
			{
				//				String channelKey = GcmHelper.getGCMRegistrationId(parent, null);
				//String channelKey = GcmHelper.getRegistrationId();
				//PMWebServiceController.device_update_channel_key(channelKey);
	
				//registration good go to inbox
				result = PanaceaSDK.Result.REGISTER_SUCCESS;
				
				System.out.println("Device Check Status Success: " + message);
			}
			else
			{
				//clear DB
				
				PMMessageStore.getInstance().deleteMessageCache();
	
				//clear settings and dont allow access to app
				PMPreferencesHelper.clearAllSettings();
	
				result = PanaceaSDK.Result.NOT_VERIFIED;
				message = "This Device is not verified. Please re-verify to proceed";
				
				System.out.println("Device Check Status Failure: " + message);
			}
	
			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.REGISTER, result, status, message,PMWebServiceController.IntentAction.WebService.DEVICE_CHECK_STATUS);

			System.out.println("Device Check Status success - Result:"+ result + " Message:");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void postError(Exception e) {
		System.out.println("Device Status Check error: " + e);

	}

}
