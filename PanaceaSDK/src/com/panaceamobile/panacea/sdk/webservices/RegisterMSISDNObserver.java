package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMDictionary;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class RegisterMSISDNObserver implements Observer {

	String phoneNumber;
	public RegisterMSISDNObserver(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
	
	public void postCompleted(String data) {
		// TODO Auto-generated method stub
		System.out.println("Register MSISDN returns:" + data);
		PMBaseResponse response;
		try {
			response = new PMBaseResponse(data);
			//Generic response - these variables will be changed for the specific responses
			String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
			int status = response.getStatus();
			String message = response.getDescription();
		
		//save phone number after success
			if (response.getStatus() == PMConstants.Status.OK)
			{
			//save phone number
				PMPreferencesHelper.setPhoneNumber(phoneNumber);

				result = PanaceaSDK.Result.REQUEST_VERIFICATION_CODE;
			}

		//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.REGISTER, result,
			status, message, PMWebServiceController.IntentAction.WebService.DEVICE_REGISTER_MSISDN);

			System.out.println("Register MSISDN success - Result:"+ result + " Message:");
			
			
		} catch (Exception e) {
			System.out.println("Register MSISDN error: "+  e.getClass().getName()+ "message:" +e.getMessage());
		}

		
	}
	

	public void postError(Exception e) {
		System.out.println("Register MSISDN error: "+  e.getClass().getName()+ "message:" +e.getMessage());
	}

}
