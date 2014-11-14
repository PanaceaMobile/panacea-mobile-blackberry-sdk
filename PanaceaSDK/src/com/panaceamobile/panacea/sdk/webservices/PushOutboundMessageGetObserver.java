package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMDictionary;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class PushOutboundMessageGetObserver implements Observer {

	public void postCompleted(String data) {
		PMBaseResponse response;
		try {
			System.out.println("Push Outbound Message Get returns result:"+ data);
			response = new PMBaseResponse(data);
		 

			//Generic response - these variables will be changed for the specific responses
			String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
			int status = response.getStatus();
			String message = response.getDescription();
	
			//save messages device settings after a successful response
			if (response.getStatus() == PMConstants.Status.OK)
			{
				Object respObject = response.getDetails();
	
				if (respObject instanceof PMDictionary)
				{
					PMDictionary details = (PMDictionary) respObject;
					PMReceivedMessage msg = new PMReceivedMessage(details);
	
					PMMessageStore.getInstance().addInboxMessage(msg);
					
					//PMWebServiceController.checkNotifications();
	
					result = PanaceaSDK.Result.MESSAGE_UPDATED_RECEIVED;
				}
			}
	
			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.MESSAGE_RECEIVED, result,
				status, message, PMWebServiceController.IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_GET);

			System.out.println("Push Outbound Message Get success - Result:"+ result + " Message:");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void postError(Exception e) {
		//retry the query

	}

}
