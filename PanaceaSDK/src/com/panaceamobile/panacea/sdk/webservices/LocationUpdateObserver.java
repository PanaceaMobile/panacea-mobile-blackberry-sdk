package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class LocationUpdateObserver implements Observer {

	public void postCompleted(String data) {
		PMBaseResponse response;
		try {
			System.out.println("Location Update returns result:"+ data);
			response = new PMBaseResponse(data);


			//Generic response - these variables will be changed for the specific responses
			String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
			int status = response.getStatus();
			String message = response.getDescription();
	
			//save messages device settings after a successful response
			if (response.getStatus() == PMConstants.Status.OK)
			{
				result = PanaceaSDK.Result.LOCATION_UPDATED;
				System.out.println("Location Get : "+message);
			}
	
			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.LOCATION, result,
				status, message, PMWebServiceController.IntentAction.WebService.DEVICE_LOCATION_UPDATE);

			System.out.println("Location Get Success - Result:"+ result + " Message:");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void postError(Exception e) {
		// TODO Auto-generated method stub
		System.out.println("Location Get Failure: "+e.getMessage());

	}

}
