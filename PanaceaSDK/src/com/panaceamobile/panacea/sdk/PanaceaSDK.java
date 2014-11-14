package com.panaceamobile.panacea.sdk;

import java.util.Hashtable;
import java.util.Vector;

import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessage;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;
import com.panaceamobile.panacea.sdk.exception.PMInvalidPhoneNumberException;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.push.LinkedHashtable;
import com.panaceamobile.panacea.sdk.webservices.PMWebServiceController;
import com.panaceamobile.panacea.ui.PanaceaTheme;
import com.panaceamobile.panacea.ui.controllers.PMScreenController;

/**
 * Point of initialization for user of the SDK. All interaction with the Panacea
 * SDK should be done through this object
 * 
 * @author Cobi Interactive
 **/
public class PanaceaSDK 
{
	private static final String TAG = "PanaceaSDK";
	private static final String BUILD = "1.0.0 bld 5";

	private static PanaceaSDK instance;
	private PMWebServiceController webserviceController;
	private PMScreenController screenController;
	private PanaceaTheme theme;
	public static void startService(Hashtable data)
	{
		instance.webserviceController.onHandleIntent(data);
	}
	 	
	/**
	 * Access to a static instance of PanaceaSDK
	 * 
	 * @return static instance of the already created object, otherwise null
	 */
	public static PanaceaSDK getInstance()
	{
		return instance;
	}

	/**
	 * Create the Panacea object with your application key and push notification
	 * key
	 * 
	 * @param applicationKey
	 *        application key provided by Panacea
	 * @param parent
	 *        the android activity - cannot be null
	 */
	public PanaceaSDK(String applicationKey)
	{

		PMPreferencesHelper.setApplicationKey(applicationKey);

		instance = this;
		webserviceController = new PMWebServiceController();
		theme = new PanaceaTheme();
		
	}
	public PanaceaTheme getTheme()
	{
		return theme;
	}
	

	/**
	 * This single method can be called by the implementer to use the baked in
	 * UI.
	 */
	public void showUI()
	{
		screenController = new PMScreenController();
		
	}
	
	public String getBuildNumber()
	{
		return BUILD;
	}

	/**
	 * Used to check if there are any active web service calls in
	 * {@link PMWebServiceController}
	 * 
	 * @return true if there are active web calls, otherwise false
	 */
	public boolean isBusy()
	{
		return PMWebServiceController.isBusy();
	}

	/* PUBLIC REGISTER WEB CALLS */

	/**
	 * The first web call done in the registration process. This method takes
	 * care of GCM registration automatically - if the GCM key has changed the
	 * Panacea server will be notified. Then determines if the device needs to
	 * be registered or only checked for verification.
	 */
	public void registerDevice()
	{
		
		
		if (PMPreferencesHelper.hasDeviceConfigurationChanged())
		{
			PMMessageStore.getInstance().deleteMessageCache();
			PMWebServiceController.device_register();
		}
		else
		{
			if (PMPreferencesHelper.getDeviceSignature() == null)
			{
				//if there is no signature, we need to re-register
				PMMessageStore.getInstance().deleteMessageCache();
				PMWebServiceController
					.device_register();
			}
			else
			{
				if (PMPreferencesHelper.isVerified())
				{
					//if there is a signature, we ensure it is still verified
					PMWebServiceController.device_check_status();
				}
				else
				{
					PMWebServiceController.device_register();
				}
			}
		}
	}

	/**
	 * If the notification returned by {@link #registerDevice} is
	 * {@link Result#REQUEST_PHONE_NUMBER} the user should be prompted for phone
	 * number then this method should be called with the number. format should
	 * be international format with +country code
	 * 
	 * @param phoneNumber
	 *        String in international format with +country code
	 * @throws PMInvalidPhoneNumberException
	 *         if the phone number does not pass validation
	 */
	public void registerPhoneNumber(String phoneNumber) throws PMInvalidPhoneNumberException
	{
		//validate phone number
		if (!PMUtils.isValidPhoneNumber(phoneNumber))
			throw new PMInvalidPhoneNumberException("The phone number is invalid.");

		//if there is no device signature we register again (device_register)
		//if the device is already verified we go to the inbox
		
		if (PMPreferencesHelper.getDeviceSignature() == null || PMPreferencesHelper.isVerified())
		{
			registerDevice();
			return;
		}

		PMWebServiceController
			.device_register_msisdn(phoneNumber);
	}

	/**
	 * If the notification returned by {@link #registerPhoneNumber} is
	 * {@link Result#REQUEST_VERIFICATION_CODE} the user should be prompted for
	 * the verification code this method should be called with the code.
	 * 
	 * @param verificationCode
	 *        sent via SMS (text)
	 */
	public void registerVerification(String verificationCode)
	{
		if (PMUtils.isBlankOrNull(verificationCode))
			return;

		//if there is no device signature we register again (device_register)
		//if the device is already verified we go to the inbox
		if (PMPreferencesHelper.getDeviceSignature() == null || PMPreferencesHelper.isVerified())
		{
			registerDevice();
			return;
		}

		PMWebServiceController.device_verification(verificationCode);
	}

	/* PUBLIC LOCATION WEB CALLS */

	/**
	 * Determines the best available location and updates the Panacea server.
	 */
//	public void updateLocation()
//	{
//		LocationManager locationManager = (LocationManager) mContext
//			.getSystemService(Context.LOCATION_SERVICE);
//		Criteria criteria = new Criteria();
//		String provider = locationManager.getBestProvider(criteria, false);
//		Location location = locationManager.getLastKnownLocation(provider);
//
//		if (location == null) //low powered solution
//		{
//			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//			criteria.setPowerRequirement(Criteria.POWER_LOW);
//			provider = locationManager.getBestProvider(criteria, false);
//			location = locationManager.getLastKnownLocation(provider);
//		}
//
//		//Log.d(TAG, "Location Updated: " + provider + " : " + location);
//
//		if (location == null)
//		{
//			return;
//		}
//		PMWebServiceController.device_location_update( location); //fire and forget
//	}

	/* PUBLIC MESSAGE WEB CALLS */

	/**
	 * Checks for new messages.
	 * 
	 * @param forceFullRefresh
	 *        if true deletes all existing data and downloads again
	 */
	public void updateMessages(boolean forceFullRefresh)
	{
		//redownload all messages
		if (forceFullRefresh)
		{
			getDB().deleteMessageCache();
		}

		//(last_id, start_date, end_date, limit, page, sort, direction)
		PMWebServiceController.device_push_outbound_messages_list(
			getDB().getLastReceivedMessageId(), null, null, new Integer(20), null, null, null);

		PMWebServiceController.device_push_inbound_messages_list(
			getDB().getLastSentMessageId(), null, null, new Integer(20), null, null, null);

	}


	/**
	 * Marks all messages in the given thread as read. The updated messages are
	 * downloaded from the server after the update.
	 * 
	 * @param threadId
	 *        of message(s) to mark as read
	 */
	public void markThreadAsRead(int threadId)
	{
		Vector messages = getDB().getMessagesForThreadId(threadId);
		Vector messagesToRead = new Vector();
		for (int i = 0;i<messages.size();i++)
		{
			PMMessage message = (PMMessage)messages.elementAt(i);
			if (message instanceof PMReceivedMessage)
			{
				PMReceivedMessage msg = (PMReceivedMessage) message;
				if (msg.isUnread())
				{
					PMWebServiceController.device_push_outbound_message_update(new Integer (msg.getReceivedMessageId()));
					messagesToRead.addElement(msg);
				}
			}
		}
		PMMessageStore.getInstance().markMultipleAsRead(messagesToRead, true);
	}
	
	/**
	 * Marks all messages in the given subject as read. The updated messages are
	 * downloaded from the server after the update.
	 * @param subject
	 */
	public void markSubjectAsRead(String subject)
	{
		Vector messages = getDB().getMessagesForSubject(subject);
		Vector messagesToRead = new Vector();
		for (int i = 0;i<messages.size();i++)
		{
			PMMessage message = (PMMessage)messages.elementAt(i);
			if (message instanceof PMReceivedMessage)
			{
				PMReceivedMessage msg = (PMReceivedMessage) message;
				if (msg.isUnread())
				{
					PMWebServiceController.device_push_outbound_message_update(new Integer(msg.getReceivedMessageId()));
					messagesToRead.addElement(msg);
				}
			}
		}
		PMMessageStore.getInstance().markMultipleAsRead(messagesToRead, true);
	}


	/**
	 * Reply to a {@link PMReceivedMessage} with message String.
	 * 
	 * @param message
	 * @param originalMessage
	 */
	public void sendReply(String message, PMReceivedMessage originalMessage)
	{
		PMWebServiceController.device_push_inbound_message_send(message, new Integer(originalMessage.getReceivedMessageId()), null,new Integer( originalMessage.getThreadID()));
	}


	/* PUBLIC MESSAGE DB CALLS */

	/**
	 * Returns a sorted list of received and sent messages for the thread id.
	 * Sorted oldest to newest
	 * 
	 * @param threadId
	 * @return List of messages in given thread
	 */
	public Vector getThreadMessages(int threadId)
	{
		return getDB().getMessagesForThreadId(threadId);
	}

	/**
	 * Returns the latest received message with a given subject for each
	 * different thread id. "All updates" will return the latest received
	 * message for each subject with a different thread id.
	 * 
	 * @param subject
	 * @return List of latest receivedMessage for each theadID
	 */
	public Vector getSubjectThreads(String subject)
	{
		return getDB().getMessagesForSubject(subject);
	}

	/**
	 * Returns a list of unique subjects and their respective unread count. This
	 * includes "All updates"
	 * 
	 * @return HashMap of unique subjects and how many unread messages each
	 *         subject has
	 */
	public LinkedHashtable getSubjectCounts()
	{
		return getDB().getSubjectCounts();
	}

	/**
	 * removes the deleted flag from all messages in the database, thereby
	 * 'undeleting' them
	 */
	public void ummarkAllDeletedMessages()
	{
		getDB().unmarkAll();
	}

	/**
	 * Marks a single message as deleted. 
	 * @param message
	 */
	public void markMessageDeleted(PMMessage message)
	{
		getDB().markMessageDeleted(message, true);
	}

	/**
	 * Marks all the messages in a thread as deleted
	 * @param threadID
	 */
	public void markThreadDeleted(int threadID)
	{
		getDB().markThread(threadID, true);
	}

	/**
	 * Marks an entire subject as deleted
	 * @param subject
	 */
	public void markSubjectDeleted(String subject)
	{
		getDB().markSubject(subject, true);
	}

	/**
	 * Private method to get DB instance
	 * 
	 * @return {@link PMDatabaseHelper}
	 */
	private PMMessageStore getDB()
	{
		return PMMessageStore.getInstance();
	}

	public void updateDeviceName(String name)
	{
		PMWebServiceController.device_update_preferences(null, name);
	}
	
	/**
	 * Determines the best available location and updates the Panacea server.
	 */
	public void updateLocation()
	{
//		LocationManager locationManager = (LocationManager) mContext
//			.getSystemService(Context.LOCATION_SERVICE);
//		Criteria criteria = new Criteria();
//		String provider = locationManager.getBestProvider(criteria, false);
//		Location location = locationManager.getLastKnownLocation(provider);
//
//		if (location == null) //low powered solution
//		{
//			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//			criteria.setPowerRequirement(Criteria.POWER_LOW);
//			provider = locationManager.getBestProvider(criteria, false);
//			location = locationManager.getLastKnownLocation(provider);
//		}
//
//		if (location == null)
//		{
//			return;
//		}
//		PMWebServiceController.device_location_update(mContext.getApplicationContext(), location); //fire and forget
	}
	
	/**
	 * Developer method to simulate sending a message from Panace to
	 * device/client
	 * 
	 * @param subject
	 * @param message
	 * @param thread_id
	 */
	public void debug_push_outbound_message_send(String subject, String message, Integer thread_id)
	{
		PMWebServiceController.DEVELOPER_push_outbound_message_send(subject, message, thread_id);
	}


	/**
	 * General labels given to a web service call
	 * 
	 * @author Cobi Interactive
	 */
	public static class Tag
	{
		public static final String REGISTER = "REGISTER";
		public static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
		public static final String MESSAGE_SENT = "MESSAGE_SENT";
		public static final String LOCATION = "LOCATION";

	}

	/**
	 * Results returned by
	 * {@link com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver.PMBroadcastListener}
	 * 
	 * @author Cobi Interactive
	 */
	public static class Result
	{
		/* Errors all calls can return */
		public static final String CONNECTION_ERROR = "CONNECTION_ERROR";
		public static final String GENERAL_ERROR = "GENERAL_ERROR";
		public static final String HTTP_SERVER_ERROR = "HTTP_SERVER_ERROR";

		public static final String PANACEA_ERROR_CODE = "PANACEA_ERROR_CODE";

		/* Specific Registration results */
		public static final String REQUEST_PHONE_NUMBER = "REQUIRE_PHONE_NUMBER";
		public static final String REQUEST_VERIFICATION_CODE = "REQUIRE_VERIFICATION_CODE";
		public static final String REGISTER_SUCCESS = "REGISTER_SUCCESS";
		public static final String NOT_VERIFIED = "NOT_VERIFIED";
		public static final String INVALID_VERIFICATION_CODE = "INVALID_VERIFICATION_CODE";
		public static final String PUSH_KEY_UPDATED = "PUSH_KEY_UPDATED";
		public static final String GIVEN_NAME_UPDATED = "GIVEN_NAME_UPDATED";

		/* Specific Message results */
		public static final String MESSAGE_UPDATED_RECEIVED = "MESSAGE_UPDATED_RECEIVED";
		public static final String MESSAGE_UPDATED_SENT = "MESSAGE_UPDATED_SENT";

		/* Specific Location results */
		public static final String LOCATION_UPDATED = "LOCATION_UPDATED";
	}
	
	
	
}
