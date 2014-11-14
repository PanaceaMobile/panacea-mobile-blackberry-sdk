package com.panaceamobile.panacea.sdk.webservices;

import java.util.Vector;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessage;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMArray;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMPagination;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class PushOutboundMessagesListObserver implements Observer {

	public void postCompleted(String data) {
		System.out.println("Push Outbound Messages List returns result:"+ data);
		PMBaseResponse response;
		Object respObject = new Object();
		String result = PanaceaSDK.Result.PANACEA_ERROR_CODE;
		String message = "";
		int status=-1;
		try {

			response = new PMBaseResponse(data);
		

			//Generic response - these variables will be changed for the specific responses
			
			status = response.getStatus();
			message = response.getDescription();
	
			//save messages device settings after a successful response
			if (response.getStatus() == PMConstants.Status.OK)
			{
				respObject = response.getDetails();
	
				
			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Push Inbound Messages List failed: " + e.getMessage());
		}
		if (respObject instanceof PMArray)
		{
			PMArray details = (PMArray) respObject;
			Vector messages = PMReceivedMessage
				.parseReceivedMessagesArray(details);

			//save to DB
			if(messages.size()>0)
				PMMessageStore.getInstance().addMessages(messages);
			//PMWebServiceController.checkNotifications();

//			PMPagination p = response.getPagination();
//			if (p.hasMorePages())
//			{
//				PMWebServiceController.device_push_outbound_messages_list(null, null,
//					null, new Integer(p.getLimit()), new Integer(p.getPage() + 1), null, null);
//			}

			result = PanaceaSDK.Result.MESSAGE_UPDATED_RECEIVED;
		
		}	

		System.out.println("Push Outbound Messages List" + (result.equals(PanaceaSDK.Result.PANACEA_ERROR_CODE)?" failed: ":" succeeded: ")+" - Result:"+ result +"Message:" + message);
			//Send successful broadcast
			PMLocalBroadcastReceiver.sendPostDataSuccess( Tag.MESSAGE_RECEIVED, result,
				status, message, PMWebServiceController.IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_LIST);
	}

	public void postError(Exception e) {
		// TODO Auto-generated method stub
		System.out.println("Push Inbound Messages List failed: " + e.getMessage());

	}

}
