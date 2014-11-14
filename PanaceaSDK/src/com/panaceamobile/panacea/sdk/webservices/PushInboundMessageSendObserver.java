package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMDictionary;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class PushInboundMessageSendObserver implements Observer {

	public void postCompleted(String data) {
		PMBaseResponse response;
		try {
			System.out.println("Push Inbound Message Send returns result:"+ data);
			response = new PMBaseResponse(data);
		

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
					int sentId = details.getInt("id", -1);
	
					if (sentId != -1)
					{
						PMWebServiceController.device_push_inbound_message_get(new Integer(sentId));
						return; //don't send broadcast until message is updated 
					}
	
				}
			}
	
			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.MESSAGE_SENT, result,
				status, message, PMWebServiceController.IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_SEND);

			System.out.println("Push Inbound Message Send success - Result:"+ result + " Message:");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	public void postError(Exception e) {
		// TODO Auto-generated method stub

	}

}
