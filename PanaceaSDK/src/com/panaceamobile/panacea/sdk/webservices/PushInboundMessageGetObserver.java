package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMSentMessage;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMDictionary;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class PushInboundMessageGetObserver implements Observer {

	public void postCompleted(String data) {
		PMBaseResponse response;
		try {
			System.out.println("Push Inbound Message Get returns result:"+ data);
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
				PMSentMessage msg = new PMSentMessage(details);

				PMMessageStore.getInstance().addInboxMessage(msg);

				result = PanaceaSDK.Result.MESSAGE_UPDATED_SENT;
			}
		}

		//Send successful broadcast
		PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.MESSAGE_SENT, result,
			status, message, PMWebServiceController.IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_GET);

		System.out.println("Push Inbound Message Get success - Result:"+ result + " Message:");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void postError(Exception e) {
		//request retry here

	}

}
