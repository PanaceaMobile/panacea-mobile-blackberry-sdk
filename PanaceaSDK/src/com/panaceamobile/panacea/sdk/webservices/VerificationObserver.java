package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class VerificationObserver implements Observer {

	public void postCompleted(String data) {
		PMBaseResponse response;
		try {
			System.out.println("Device Verification Result:"+ data);
			response = new PMBaseResponse(data);
		
			//Generic response - these variables will be changed for the specific responses
			String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
			int status = response.getStatus();
			String message = response.getDescription();
	
			//set verified and update channel_key if necessary 
			if (response.getStatus() == PMConstants.Status.OK)
			{
				PMPreferencesHelper.setVerified(true);
	
			
				//registration good go to inbox
				result = PanaceaSDK.Result.REGISTER_SUCCESS;
			}
			else if (response.getStatus() == PMConstants.Status.GENERIC_ERROR)
			{
				result = PanaceaSDK.Result.INVALID_VERIFICATION_CODE;
			}
	
			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.REGISTER, result,
				status, message, PMWebServiceController.IntentAction.WebService.DEVICE_REGISTER_VERIFICATION);

			System.out.println("Verification success - Result:"+ result + " Message:");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void postError(Exception e) {
		System.out.println("Verification error: " + e.getMessage());

	}

}
