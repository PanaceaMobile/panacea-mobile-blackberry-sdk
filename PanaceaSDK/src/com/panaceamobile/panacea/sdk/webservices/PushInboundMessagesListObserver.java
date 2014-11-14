package com.panaceamobile.panacea.sdk.webservices;

import java.util.Vector;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessage;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMSentMessage;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMArray;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMPagination;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class PushInboundMessagesListObserver implements Observer {

	public void postCompleted(String data) {
		System.out.println("Push Inbound Messages List returns result:"+ data);
		
		PMBaseResponse response;
		try {
			response = new PMBaseResponse(data);
		
		//Generic response - these variables will be changed for the specific responses
		String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
		int status = response.getStatus();
		String message = response.getDescription();

		//save messages device settings after a successful response
		if (response.getStatus() == PMConstants.Status.OK)
		{
			Object respObject = response.getDetails();

			if (respObject instanceof PMArray)
			{
				PMArray details = (PMArray) respObject;
				Vector messages = PMSentMessage.parseSentMessagesArray(details);
				if(messages.size()>0)
					PMMessageStore.getInstance().addMessages(messages);

				PMPagination p = response.getPagination();
				if (p.hasMorePages())
				{
					PMWebServiceController.device_push_inbound_messages_list(null, null,
						null, new Integer(p.getLimit()), new Integer(p.getPage() + 1), null, null);
				}

				result = PanaceaSDK.Result.MESSAGE_UPDATED_SENT;
			}
		}
		System.out.println("Push Inbound Messages List" + (result.equals(PanaceaSDK.Result.PANACEA_ERROR_CODE)?" failed: ":" succeeded: ") + message);

		//Send successful broadcast
		PMLocalBroadcastReceiver.sendPostDataSuccess(Tag.MESSAGE_SENT, result,
			status, message, PMWebServiceController.IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_LIST);

		System.out.println("Push Inbound Messages List success - Result:"+ result + " Message:");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Push Inbound Messages List failed: " +e.getMessage());
		
		}

		
	}

	public void postError(Exception e) {
		//retry the query
		System.out.println("Push Inbound Messages List failed: " +e.getMessage());
		
	}

}
