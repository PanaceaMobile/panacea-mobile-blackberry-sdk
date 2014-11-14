package com.panaceamobile.panacea.sdk.webservices;

import org.json.me2.JSONException;
import org.json.me2.JSONObject;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMDictionary;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class RegisterWebserviceObserver implements Observer {

	private String channel_key;
	private String given_name;
	public RegisterWebserviceObserver(String channel_key, String given_name)
	{
		this.channel_key = channel_key;
		this.given_name = given_name;
	}
	
	public void postCompleted(String data) {
			System.out.println("Device Register returns result:"+ data);
		PMBaseResponse response;
		try {
			response = new PMBaseResponse(new JSONObject(data));

			//Generic response - these variables will be changed for the specific responses
			String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
			int status = response.getStatus();
			String message = response.getDescription();
	
			if (response.getStatus() == PMConstants.Status.OK)
			{
				Object respObject = response.getDetails();
	
				if (respObject instanceof PMDictionary)
				{
					PMDictionary details = (PMDictionary) respObject;
					String signature = details.getString("signature", null);
					boolean verified = details.getBool("verified", false);
					PMPreferencesHelper.setDeviceSignature(signature);
					PMPreferencesHelper.setVerified(verified);
					PMPreferencesHelper.setDeviceConfiguration();
	
					//if push notification key was included
					if (channel_key != null)
					{
						PMPreferencesHelper.setPushNotificationKey(channel_key);
					}
					if(given_name!=null)
					{
						PMPreferencesHelper.setGivenName(given_name);
					}
					if (PMPreferencesHelper.isVerified())
					{
						result = PanaceaSDK.Result.REGISTER_SUCCESS;
					}
					else
					{
						result = PanaceaSDK.Result.REQUEST_PHONE_NUMBER;
					}
				}
			}
			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.REGISTER, result,
				status, message, PMWebServiceController.IntentAction.WebService.DEVICE_REGISTER);
			
			System.out.println("Device Register success - Result:"+ result + " Message:");
		
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public void postError(Exception e) {
		int result = PMWebServiceController.Result.FAILURE_SERVER_UNAVAILABLE;
		String resultString = PanaceaSDK.Result.CONNECTION_ERROR;
		String message = "Server Unavailable. Please try again later.";
		
		PMLocalBroadcastReceiver.sendPostDataFailure(Tag.REGISTER, resultString,
				-1, message, PMWebServiceController.IntentAction.WebService.DEVICE_REGISTER);

	}
	
	

}
