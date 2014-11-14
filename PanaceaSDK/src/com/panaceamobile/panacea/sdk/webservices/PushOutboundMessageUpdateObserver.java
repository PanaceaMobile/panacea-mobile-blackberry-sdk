package com.panaceamobile.panacea.sdk.webservices;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

public class PushOutboundMessageUpdateObserver implements Observer {
private Integer outboundMessageId;
	public PushOutboundMessageUpdateObserver(Integer outboundMessageId)
	{
		this.outboundMessageId = outboundMessageId;
	}
	public void postCompleted(String data) {
		System.out.println("Push Outbound Message Update returns result:"+ data);
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
				Integer updatedMsgId = outboundMessageId;
	
				//request update from server
				PMWebServiceController.device_push_outbound_message_get(updatedMsgId);	
				
				
				
				return; //don't send broadcast until message is updated
				
			
			}
			
			//Send successful broadcast
				PMLocalBroadcastReceiver.sendPostDataFailure(Tag.MESSAGE_RECEIVED, result,
				status, message, PMWebServiceController.IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_UPDATE);
			
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void postError(Exception e) {
		// TODO Add code to retry the query.

	}

}
