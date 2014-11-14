package com.panaceamobile.panacea.sdk.webservices;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.ApplicationDescriptor;

import com.panaceamobile.panacea.sdk.PMConstants;
import com.panaceamobile.panacea.sdk.PMParams;
import com.panaceamobile.panacea.sdk.PMPreferencesHelper;
import com.panaceamobile.panacea.sdk.PMUtils;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMDatabaseHelper;
import com.panaceamobile.panacea.sdk.db.PMMessage;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;
import com.panaceamobile.panacea.sdk.db.PMSentMessage;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver;
import com.panaceamobile.panacea.sdk.messaging.PMLocalBroadcastReceiver.PMBroadcastListener;
import com.panaceamobile.panacea.sdk.model.PMArray;
import com.panaceamobile.panacea.sdk.model.PMBaseResponse;
import com.panaceamobile.panacea.sdk.model.PMDictionary;
import com.panaceamobile.panacea.sdk.model.PMPagination;
import com.panaceamobile.panacea.sdk.web.HttpPost;
import com.panaceamobile.panacea.sdk.web.HttpPost.Observer;

/**
 * This Class handles the initiation and response handling of
 * all WebService calls. When the call and response handling is completed a a
 * local broadcast is send via {@link PMLocalBroadcastReceiver}.
 * 
 * @author Cobi Interactive
 * @see PMLocalBroadcastReceiver
 */
public class PMWebServiceController implements PMBroadcastListener
{
		
	public static final String TAG = "PMWebServiceController";

	/**
	 * Used to keep track of the number of active webservices for each method
	 */
	private static Hashtable busyRequests = new Hashtable();

	/**
	 * Keeps track of the number of active requests.
	 * 
	 * @param method
	 */
	private synchronized void sendRequestStarted(String method)
	{
		if (method == null)
			return;

		if (busyRequests == null)
			busyRequests = new Hashtable();

		Integer requestCount = (Integer)busyRequests.get(method);
		if (requestCount == null)
		{
			busyRequests.put(method, new Integer(1));
		}
		else
		{
			busyRequests.put(method, new Integer(requestCount.intValue() + 1));
		}
	}

	/**
	 * Keeps track of the number of active requests.
	 * 
	 * @param method
	 */
	private synchronized void sendRequestEnded(String method)
	{
		if (method == null)
			return;

		if (busyRequests == null)
			busyRequests = new Hashtable();

		Integer requestCount = (Integer)busyRequests.get(method);
		if (requestCount != null)
		{
			if (requestCount.intValue() <= 1)
			{
				busyRequests.remove(method);
			}
			else
			{
				busyRequests.put(method, new Integer(requestCount.intValue()-1));
			}
		}

	}

	/**
	 * Check if there are any busy Panacea web requests.
	 * 
	 * @return true if there are any requests, otherwise false
	 */
	public static boolean isBusy()
	{
		if (busyRequests == null)
			return false;

		return busyRequests.size() != 0;
	}

	public PMWebServiceController()
	{
		//super(PMWebServiceController.class.getName());
	}


	public void onHandleIntent(Hashtable data)
	{
		String action = (String)data.get("Action");
		
		
		/* REGISTRATION */
		if (IntentAction.WebService.DEVICE_REGISTER.equals(action))
		{
			handle_device_register(data);
		}
		else if (IntentAction.WebService.DEVICE_REGISTER_MSISDN.equals(action))
		{
			handle_device_register_msisdn(data);
		}
		else if (IntentAction.WebService.DEVICE_REGISTER_VERIFICATION.equals(action))
		{
			handle_device_verification(data);
		}
		else if (IntentAction.WebService.DEVICE_CHECK_STATUS.equals(action))
		{
			handle_device_check_status(data);
		}
		else if (IntentAction.WebService.DEVICE_UPDATE_PREFERENCES.equals(action))
		{
			handle_device_update_preferences(data);
		}

		/* LOCATION */
		else if (IntentAction.WebService.DEVICE_LOCATION_GET.equals(action))
		{
			handle_device_location_get(data);
		}
		else if (IntentAction.WebService.DEVICE_LOCATION_UPDATE.equals(action))
		{
			handle_device_location_update(data);
		}

		/* INBOX */
		else if (IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_LIST.equals(action))
		{
			handle_device_push_outbound_messages_list(data);
		}
		else if (IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_GET.equals(action))
		{
			handle_device_push_outbound_message_get(data);
		}
		else if (IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_UPDATE.equals(action))
		{
			handle_device_push_outbound_message_update(data);
		}

		/* OUTBOX */
		else if (IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_SEND.equals(action))
		{
			handle_device_push_inbound_message_send(data);
		}
		else if (IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_LIST.equals(action))
		{
			handle_device_push_inbound_messages_list(data);
		}
		else if (IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_GET.equals(action))
		{
			handle_device_push_inbound_message_get(data);
		}


		else if ("push_outbound_message_send".equals(action))
		{
			handle_DEVELOPER_push_outbound_message_send(data);
		}

	}


	/**
	 * Schedules a retry. the retry wait is multiplied by a factor of 2 with
	 * every successive retry. the retries are limited to 20 attempts.
	 * 
	 * @param intent
	 */
//	private void queueRetry(Intent intent)
//	{
//		/* Do calculations for retry back off */
//		//cancel retry after so many retries
//		int retries = intent.getIntExtra("RETRY_NUMBER", 0);
//		Log.d(TAG, "retries: " + retries);
//		if (retries >= 20)
//		{
//			return;
//		}
//		else
//		{
//			intent.putExtra("RETRY_NUMBER", ++retries);
//		}
//
//		//Next retry
//		int nextRetryIn = intent.getIntExtra("NEXT_RETRY_IN", 10);
//		intent.putExtra("NEXT_RETRY_IN", nextRetryIn * 2);
//
//		Calendar now = Calendar.getInstance();
//		// define intent to run each time alarm is triggered
//		PendingIntent pending = PendingIntent.getService(getApplicationContext(), 0, intent,
//			PendingIntent.FLAG_CANCEL_CURRENT);
//
//		// set retry for next retry
//		now.add(Calendar.SECOND, nextRetryIn);
//		long newAlarm = now.getTimeInMillis();
//		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(
//			Context.ALARM_SERVICE);
//		alarmManager.set(AlarmManager.RTC, newAlarm, pending);
//	}

	/**
	 * Generic Panacea sendRequest.
	 * <p>
	 * Unpacks URL parameters and performs web request.
	 * <p>
	 * Tracks active webcalls by using {@link #sendRequestStarted(String)} and
	 * {@link #sendRequestEnded(String)}
	 * <p>
	 * Notifies of failure case
	 * 
	 * @param method
	 *        {@link IntentAction.WebService}
	 * @param tag
	 * @param params
	 *        {@link PMParams}
	 * @return {@link WSResponse}
	 */
	private void sendRequest(String method, String tag, PMParams params,Observer obs)
	{
		
		
		String postURL = PMConstants.PANACEA_API_URL + "?action=" + method
			+ (params != null ? params.getURLParameters() : "");

		
		sendRequestStarted(method);

		//Send PreDataExecute Broadcast
		PMLocalBroadcastReceiver.sendPreDataExecute(tag);

					

		// call service
		HttpPost.post(postURL, null, "application/json", obs);

		

	}


	/* REGISTER CALLS */

	/**
	 * Starts the {@link IntentAction.WebService#DEVICE_REGISTER} WebService.
	 * 
	 * @param context
	 * @param channelKey
	 *        Push Notification Channel Key
	 * @see #handle_device_register
	 */
	public static void device_register()
	{
	
		Hashtable data = new Hashtable();
		data.put("Action",IntentAction.WebService.DEVICE_REGISTER);

		/* MANDATORY PARAMETERS */

		String application_key = PMPreferencesHelper.getApplicationKey();
		String unique_device_id = PMPreferencesHelper.getUniqueDeviceId();
		String device_manufacturer = PMPreferencesHelper.getDeviceManufacturer();
		String device_model = PMPreferencesHelper.getDeviceModel();
		String operating_system = PMPreferencesHelper.getOpereatingSystem();
		String operating_system_version = PMPreferencesHelper.getOperatingSystemVersion();
		String timezone = PMPreferencesHelper.getTimezone();
		String locale = PMPreferencesHelper.getLocale();
		
		
		String push_channel_id = PMPreferencesHelper.getPushChannelId();

		if (application_key == null || unique_device_id == null || device_manufacturer == null
			|| device_model == null || operating_system == null || operating_system_version == null
			|| timezone == null || locale == null || push_channel_id == null)
			return;

		String channelKey = PMPreferencesHelper.getPushNotificationKey();
		String givenName = PMPreferencesHelper.getGivenName();
		
		data.put("application_key", application_key);
		data.put("unique_device_id", unique_device_id);
		data.put("device_manufacturer", device_manufacturer);
		data.put("device_model", device_model);
		data.put("operating_system", operating_system);
		data.put("operating_system_version", operating_system_version);
		data.put("timezone", timezone);
		data.put("locale", locale);
		data.put("push_channel_id", push_channel_id);
		if(channelKey!=null)
			data.put("channel_key", channelKey);
		if(givenName!=null)
			data.put("given_name", givenName);
		
		PanaceaSDK.startService(data);
	}
	
	
	
	
	
	/**
	 * Processes {@link Intent} from {@link #device_register(Context, String)}
	 * 
	 * @param data
	 * @see #device_register(Context, String)
	 */
	private void handle_device_register(Hashtable data)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)data.get("application_key");
		String unique_device_id =(String) data.get("unique_device_id");
		String device_manufacturer =(String) data.get("device_manufacturer");
		String device_model =(String) data.get("device_model");
		String operating_system = (String)data.get("operating_system");
		String operating_system_version =(String) data.get("operating_system_version");
		String timezone = (String)data.get("timezone");
		String locale = (String)data.get("locale");
		String push_channel_id = (String)data.get("push_channel_id");
		String given_name = (String)data.get("given_name");

		params.put("application_key", application_key);
		params.put("unique_device_id", unique_device_id);
		params.put("device_manufacturer", device_manufacturer);
		params.put("device_model", device_model);
		params.put("operating_system", operating_system);
		params.put("operating_system_version", operating_system_version);
		params.put("timezone", timezone);
		params.put("locale", locale);
		params.put("push_channel_id", push_channel_id);

		/* OPTIONAL PARAMETERS */
		String channel_key = (String)data.get("channel_key");

		if (channel_key != null)
			params.put("channel_key", channel_key);
		if(given_name!=null)
			params.put("given_name",given_name);

		String method = (String)data.get("Action");
		String tag = Tag.REGISTER;

		System.out.println("Sending Request: handle_device_register");
		sendRequest(method, tag, params, new RegisterWebserviceObserver(channel_key,given_name));



		//no retry for register
	}

	/**
	 * Starts the {@link IntentAction.WebService#DEVICE_REGISTER_MSISDN}
	 * WebService.
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	public static void device_register_msisdn(String phoneNumber)
	{

		Hashtable data = new Hashtable();
		data.put("Action", IntentAction.WebService.DEVICE_REGISTER_MSISDN);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		data.put("application_key", appKey);
		data.put("device_signature", devSignature);
		data.put("msisdn", phoneNumber);

		PanaceaSDK.startService(data);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_register_msisdn(Context, String)}
	 * 
	 * @param intent
	 * @see #device_register_msisdn(Context, String)
	 */
	private void handle_device_register_msisdn(Hashtable data)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)data.get("application_key");
		String device_signature = (String) data.get("device_signature");
		String msisdn =  (String) data.get("msisdn");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		params.put("msisdn", msisdn);

		String method =  (String)data.get("Action");
		String tag = Tag.REGISTER;
		
		Observer obs = new RegisterMSISDNObserver(msisdn);
		System.out.println("Sending Request: handle_device_register_msisdn");
		sendRequest(method, tag, params, obs);

		
	}

	/**
	 * Starts the {@link IntentAction.WebService#DEVICE_REGISTER_VERIFICATION}
	 * WebService.
	 * 
	 * @param context
	 * @param verificationCode
	 */
	public static void device_verification(String verificationCode)
	{
		if (verificationCode == null)
			return;

		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_REGISTER_VERIFICATION);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);
		intent.put("verification_code", verificationCode);

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_verification(Context, String)}
	 * 
	 * @param intent
	 * @see #device_verification(Context, String)
	 */
	private void handle_device_verification(Hashtable data)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)data.get("application_key");
		String device_signature = (String)data.get("device_signature");
		String verification_code = (String)data.get("verification_code");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		params.put("verification_code", verification_code);

		String method = (String)data.get("Action");
		String tag = Tag.REGISTER;

		System.out.println("Sending Request: handle_device_verification");
		sendRequest(method, tag, params, new VerificationObserver());

	
	}

	/**
	 * Starts the {@link IntentAction.WebService#DEVICE_CHECK_STATUS}
	 * WebService.
	 * 
	 * @param context
	 */
	public static void device_check_status()
	{

		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_CHECK_STATUS);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from {@link #device_check_status(Context)}
	 * 
	 * @param intent
	 * @see #device_check_status(Context)
	 */
	private void handle_device_check_status(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String) intent.get("device_signature");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);

		String method = (String) intent.get("Action");

		Observer obs = new DeviceCheckStatusObserver();
		
		System.out.println("Sending Request: handle_device_check_status");
		sendRequest(method, Tag.REGISTER, params, obs);

		
		//Queue retry if connection error
//		else if (Result.FAILURE_SERVER_UNAVAILABLE == rsp.result)
//		{
//			queueRetry(intent);
//		}
	}

	/**
	 * Starts the {@link IntentAction.WebService#DEVICE_CHECK_STATUS}
	 * WebService.
	 * <p>
	 * Updates the devices Push Notification Channel Key. Should be called if
	 * the device gets a new GCM Registration ID. Can only be called on Verified
	 * devices.
	 * 
	 * @param context
	 * @param notificationKey
	 *        the GCM Registration ID for push notifications
	 */
	public static void device_update_preferences(String channelKey, String givenName)
	{
		
		
		//only if the user is verified
		if (!PMPreferencesHelper.isVerified())
			return;
		
		
		
		//only if its different from current key or different name
		if (channelKey!=null && channelKey.equals(PMPreferencesHelper.getPushNotificationKey()) && givenName!=null && givenName.equals(PMPreferencesHelper.getGivenName()))
			return;

		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_UPDATE_PREFERENCES);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		if(!PMUtils.isBlankOrNull(channelKey))
			intent.put("channel_key", channelKey);
		if(!PMUtils.isBlankOrNull(givenName))
			intent.put("given_name", givenName);
		
		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_update_channel_key(Context, String)}
	 * 
	 * @param intent
	 * @see #device_update_channel_key(Context, String)
	 */
	private void handle_device_update_preferences(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String)intent.get("device_signature");
		String channel_key =(String) intent.get("channel_key");
		String given_name = (String) intent.get("given_name");
		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		
		if(!PMUtils.isBlankOrNull(channel_key))
			params.put("channel_key", channel_key);
		if(!PMUtils.isBlankOrNull(given_name))
			params.put("given_name",given_name);

		String method = (String)intent.get("Action");
		String tag = Tag.REGISTER;
		Observer obs = new UpdatePreferencesObserver(channel_key,given_name);
		System.out.println("Sending Request: handle_device_update_channel_key");
		sendRequest(method, tag, params,obs);

		
		//no retry for register
	}

	/* LOCATION CALLS */

	/**
	 * Starts the {@link IntentAction.WebService#DEVICE_LOCATION_GET}
	 * WebService.
	 * 
	 * @param context
	 */
	public static void device_location_get()
	{
		

		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_LOCATION_GET);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from {@link #device_location_get(Context)}
	 * 
	 * @param intent
	 * @see #device_location_get(Context)
	 */
	private void handle_device_location_get(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String)intent.get("device_signature");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);

		String method =(String) intent.get("Action");
		String tag = Tag.LOCATION;
		Observer obs = new LocationGetObserver();
		System.out.println("Sending Request: handle_device_location_get");
		sendRequest(method, tag, params, obs);

	
	}

	/**
	 * Starts the {@link IntentAction.WebService#DEVICE_LOCATION_UPDATE}
	 * WebService.
	 * 
	 * @param context
	 * @param location
	 */
//	public static void device_location_update(Location location)
//	{
//		if (location == null)
//			return;
//
//		Hashtable intent = new Hashtable();
//		intent.put("Action",IntentAction.WebService.DEVICE_LOCATION_UPDATE);
//
//		/* MANDATORY PARAMETERS */
//		String appKey = PMPreferencesHelper.getApplicationKey();
//		String devSignature = PMPreferencesHelper.getDeviceSignature();
//
//		if (appKey == null || devSignature == null)
//			return;
//
//		intent.put("application_key", appKey);
//		intent.put("device_signature", devSignature);
//		intent.put("longitude", location.getLongitude() + "");
//		intent.put("latitude", location.getLatitude() + "");
//		intent.put("source",
//			LocationManager.GPS_PROVIDER.equals(location.getProvider()) ? "gps" : "network");
//		intent.put("accuracy", location.getAccuracy() + "");
//
//		PanaceaSDK.startService(intent);
//	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_location_update(Context, Location)}
	 * 
	 * @param intent
	 * @see #device_location_update(Context, Location)
	 */
	private void handle_device_location_update(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String)intent.get("device_signature");
		String longitude = (String)intent.get("longitude");
		String latitude = (String)intent.get("latitude");
		String source =(String) intent.get("source");
		String accuracy =(String) intent.get("accuracy");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("source", source);
		params.put("accuracy", accuracy);

		String method = (String)intent.get("Action");
		String tag = Tag.LOCATION;
		Observer obs = new LocationUpdateObserver();
		System.out.println("Sending Request: handle_device_location_update");
		sendRequest(method, tag, params, obs);

		
	}

	/* INBOX MESSAGE CALLS */

	/**
	 * Starts the
	 * {@link IntentAction.WebService#DEVICE_PUSH_OUTBOUND_MESSAGE_LIST}
	 * WebService.
	 * 
	 * @param context
	 */
	public static void device_push_outbound_messages_list(Integer last_id,
		Date start_date, Date end_date, Integer limit, Integer page, String sort, String direction)
	{
		
		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_LIST);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);

		/* OPTIONAL PARAMETERS */

		final String STRING_DATE_FORMAT = "yyyy-MM-dd";

		if (last_id != null)
			intent.put("last_id", last_id.toString());
		if (start_date != null)
			intent.put("start_date", PMUtils.dateToString(STRING_DATE_FORMAT, start_date));
		if (end_date != null)
			intent.put("end_date", PMUtils.dateToString(STRING_DATE_FORMAT, end_date));
		if (limit != null)
			intent.put("limit", limit + "");
		if (page != null)
			intent.put("page", page + "");
		if (sort != null)
			intent.put("sort", sort);
		if (direction != null)
			intent.put("direction", direction);

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_push_outbound_messages_list(Context, Integer, Date, Date, Integer, Integer, String, String)}
	 * 
	 * @param intent
	 * @see #device_push_outbound_messages_list(Context, Integer, Date, Date,
	 *      Integer, Integer, String, String)
	 */
	private void handle_device_push_outbound_messages_list(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String)intent.get("device_signature");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);

		/* OPTIONAL PARAMETERS */

		String last_id =  (String)intent.get("last_id");
		String start_date =  (String)intent.get("start_date");
		String end_date =  (String)intent.get("end_date");
		String limit =  (String)intent.get("limit");
		String page =  (String)intent.get("page");
		String sort =  (String)intent.get("sort");
		String direction =  (String)intent.get("direction");

		if (last_id != null)
			params.put("last_id", last_id);
		if (start_date != null)
			params.put("start_date", start_date);
		if (end_date != null)
			params.put("end_date", end_date);
		if (limit != null)
			params.put("limit", limit);
		if (page != null)
			params.put("page", page);
		if (sort != null)
			params.put("sort", sort);
		if (direction != null)
			params.put("direction", direction);

		String method = (String)intent.get("Action");
		String tag = Tag.MESSAGE_RECEIVED;
		
		Observer obs = new PushOutboundMessagesListObserver();
		System.out.println("Sending Request: handle_device_push_outbound_messages_list");
		sendRequest(method, tag, params, obs);

		
	}

	/**
	 * Starts the
	 * {@link IntentAction.WebService#DEVICE_PUSH_OUTBOUND_MESSAGE_UPDATE}
	 * WebService.
	 * <p>
	 * Update the message. Currently this function marks the message as being
	 * read
	 * 
	 * @param context
	 * @param outboundMessageId
	 *        id of the message to mark as read
	 */
	public static void device_push_outbound_message_update(
		Integer outboundMessageId)
	{

		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_UPDATE);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);
		intent.put("outbound_message_id", outboundMessageId);

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_push_outbound_message_update(Context, Integer)}
	 * 
	 * @param intent
	 * @see #device_push_outbound_message_update(Context, Integer)
	 */
	private void handle_device_push_outbound_message_update(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String)intent.get("device_signature");
		Integer outbound_message_id = (Integer)intent.get("outbound_message_id");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		params.put("outbound_message_id", outbound_message_id);

		String method =(String) intent.get("Action");
		String tag = Tag.MESSAGE_RECEIVED;
		
		PushOutboundMessageUpdateObserver obs = new PushOutboundMessageUpdateObserver(outbound_message_id);
		System.out.println("Sending Request: handle_device_push_outbound_message_update");
		sendRequest(method, tag, params,obs);

		

	}

	/**
	 * Starts the
	 * {@link IntentAction.WebService#DEVICE_PUSH_OUTBOUND_MESSAGE_GET}
	 * WebService.
	 * <p>
	 * Returns the details on outbound message for the given outbound id
	 * 
	 * @param context
	 * @param outboundMessageId
	 *        id of the message to retrieve
	 */
	public static void device_push_outbound_message_get(Integer outboundMessageId)
	{
		Hashtable intent = new Hashtable();
		intent.put("Action", IntentAction.WebService.DEVICE_PUSH_OUTBOUND_MESSAGE_GET);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);
		intent.put("outbound_message_id", outboundMessageId + "");

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_push_outbound_message_get(Context, Integer)}
	 * 
	 * @param intent
	 * @see #device_push_outbound_message_get(Context, Integer)
	 */
	private void handle_device_push_outbound_message_get(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature =(String) intent.get("device_signature");
		String outbound_message_id =(String) intent.get("outbound_message_id");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		params.put("outbound_message_id", outbound_message_id);

		String method = (String)intent.get("Action");
		String tag = Tag.MESSAGE_RECEIVED;
		Observer obs = new PushOutboundMessageGetObserver();
		System.out.println("Sending Request: handle_device_push_outbound_message_get");
		sendRequest(method, tag, params, obs);
	}


	/* OUTBOX MESSAGE CALLS */

	/**
	 * Starts the
	 * {@link IntentAction.WebService#DEVICE_PUSH_INBOUND_MESSAGE_SEND}
	 * WebService.
	 * 
	 * @param context
	 * @param message
	 * @param messageID
	 * @param batchID
	 * @param threadID
	 */
	public static void device_push_inbound_message_send(String message,
		Integer messageID, Integer batchID, Integer threadID)
	{

		Hashtable data = new Hashtable();
		data.put("Action",IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_SEND);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		data.put("application_key", appKey);
		data.put("device_signature", devSignature);
		data.put("data", message);

		/* OPTIONAL PARAMETERS */

		if (messageID != null)
			data.put("outbound_message_id", messageID.toString());
		if (batchID != null)
			data.put("outbound_message_batch_id", batchID.toString());
		if (threadID != null)
			data.put("thread_id", threadID.toString());

		PanaceaSDK.startService(data);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_push_inbound_message_send(Context, String, Integer, Integer, Integer)}
	 * 
	 * @param intent
	 * @see #device_push_inbound_message_send(Context, String, Integer, Integer,
	 *      Integer)
	 */
	private void handle_device_push_inbound_message_send(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String)intent.get("device_signature");
		String data = (String)intent.get("data");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		params.put("data", data);

		/* OPTIONAL PARAMETERS */

		String outbound_message_id = (String)intent.get("outbound_message_id");
		String outbound_message_batch_id =(String) intent.get("outbound_message_batch_id");
		String thread_id = (String)intent.get("thread_id");

		if (outbound_message_id != null)
			params.put("outbound_message_id", outbound_message_id);
		if (outbound_message_batch_id != null)
			params.put("outbound_message_batch_id", outbound_message_batch_id);
		if (thread_id != null)
			params.put("thread_id", thread_id);

		String method = (String)intent.get("Action");
		String tag = Tag.MESSAGE_SENT;
		Observer obs = new PushInboundMessageSendObserver();
		System.out.println("Sending Request: handle_device_push_inbound_message_send");
		sendRequest(method, tag, params, obs);
	}

	/**
	 * Starts the
	 * {@link IntentAction.WebService#DEVICE_PUSH_INBOUND_MESSAGE_LIST}
	 * WebService.
	 * 
	 * @param context
	 * @param last_id
	 * @param start_date
	 * @param end_date
	 * @param limit
	 * @param page
	 * @param sort
	 * @param direction
	 */
	public static void device_push_inbound_messages_list(Integer last_id,
		Date start_date, Date end_date, Integer limit, Integer page, String sort, String direction)
	{

		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_LIST);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);

		/* OPTIONAL PARAMETERS */

		final String STRING_DATE_FORMAT = "yyyy-MM-dd";

		if (last_id != null)
			intent.put("last_id", last_id.toString());
		if (start_date != null)
			intent.put("start_date", PMUtils.dateToString(STRING_DATE_FORMAT, start_date));
		if (end_date != null)
			intent.put("end_date", PMUtils.dateToString(STRING_DATE_FORMAT, end_date));
		if (limit != null)
			intent.put("limit", limit + "");
		if (page != null)
			intent.put("page", page + "");
		if (sort != null)
			intent.put("sort", sort);
		if (direction != null)
			intent.put("direction", direction);

		
		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_push_inbound_messages_list(Context, Integer, Date, Date, Integer, Integer, String, String)}
	 * 
	 * @param intent
	 * @see #device_push_inbound_messages_list(Context, Integer, Date, Date,
	 *      Integer, Integer, String, String)
	 */
	private void handle_device_push_inbound_messages_list(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String) intent.get("application_key");
		String device_signature = (String) intent.get("device_signature");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);

		/* OPTIONAL PARAMETERS */

		String last_id = (String)intent.get("last_id");
		String start_date = (String) intent.get("start_date");
		String end_date = (String) intent.get("end_date");
		String limit = (String) intent.get("limit");
		String page = (String) intent.get("page");
		String sort = (String) intent.get("sort");
		String direction = (String) intent.get("direction");

		if (last_id != null)
			params.put("last_id", last_id);
		if (start_date != null)
			params.put("start_date", start_date);
		if (end_date != null)
			params.put("end_date", end_date);
		if (limit != null)
			params.put("limit", limit);
		if (page != null)
			params.put("page", page);
		if (sort != null)
			params.put("sort", sort);
		if (direction != null)
			params.put("direction", direction);

		String method =  (String)intent.get("Action");
		String tag = Tag.MESSAGE_SENT;
		
		Observer obs = new PushInboundMessagesListObserver();
		System.out.println("Sending Request: handle_device_push_inbound_messages_list");
		sendRequest(method, tag, params, obs);

		

	}

	/**
	 * Starts the
	 * {@link IntentAction.WebService#DEVICE_PUSH_INBOUND_MESSAGE_GET}
	 * WebService.
	 * 
	 * @param context
	 * @param inboundMessageId
	 */
	public static void device_push_inbound_message_get(Integer inboundMessageId)
	{
		Hashtable intent = new Hashtable();
		intent.put("Action",IntentAction.WebService.DEVICE_PUSH_INBOUND_MESSAGE_GET);

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String devSignature = PMPreferencesHelper.getDeviceSignature();

		if (appKey == null || devSignature == null)
			return;

		intent.put("application_key", appKey);
		intent.put("device_signature", devSignature);
		intent.put("inbound_message_id", inboundMessageId + "");

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #device_push_inbound_message_get(Context, Integer)}
	 * 
	 * @param intent
	 * @see #device_push_inbound_message_get(Context, Integer)
	 */
	private void handle_device_push_inbound_message_get(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String device_signature = (String)intent.get("device_signature");
		String inbound_message_id = (String)intent.get("inbound_message_id");

		params.put("application_key", application_key);
		params.put("device_signature", device_signature);
		params.put("inbound_message_id", inbound_message_id);

		String method = (String)intent.get("Action");
		String tag = Tag.MESSAGE_SENT;

		
		Observer obs  = new PushInboundMessageGetObserver();
		System.out.println("Sending Request: handle_device_push_inbound_message_get");
		sendRequest(method, tag, params,obs);

		
	}

	/* DEVELOPER CALLS */

	/**
	 * Starts the "push_outbound_message_send" WebService.
	 * 
	 * @param context
	 * @param subject
	 * @param message
	 * @param thread_id
	 */
	public static void DEVELOPER_push_outbound_message_send( String subject,
		String message, Integer thread_id)
	{
		if (subject == null || message == null)
			return;

		Hashtable intent = new Hashtable();
		intent.put("Action","push_outbound_message_send");

		/* MANDATORY PARAMETERS */
		String appKey = PMPreferencesHelper.getApplicationKey();
		String phoneNumber = PMPreferencesHelper.getPhoneNumber();

		if (appKey == null || phoneNumber == null)
			return;

		intent.put("application_key", appKey);
		intent.put("msisdn", phoneNumber);
		intent.put("subject", subject);
		intent.put("message", message);

		/* THIS IS ONLY A DEBUG/DEVELOPER USERNAME/PASSWORD. */
		/* If you have your own credentials you can substitute them here */
		intent.put("username", "donald");
		intent.put("password", "abc123");

		/* OPTIONAL PARAMETERS */

		if (thread_id != null)
			intent.put("thread_id", thread_id + "");

		PanaceaSDK.startService(intent);
	}

	/**
	 * Processes {@link Intent} from
	 * {@link #DEVELOPER_push_outbound_message_send(Context, String, String, Integer)}
	 * 
	 * @param intent
	 * @see #DEVELOPER_push_outbound_message_send(Context, String, String,
	 *      Integer)
	 */
	private void handle_DEVELOPER_push_outbound_message_send(Hashtable intent)
	{
		PMParams params = new PMParams();

		/* MANDATORY PARAMETERS */
		String application_key = (String)intent.get("application_key");
		String msisdn = (String) intent.get("msisdn");
		String username = (String) intent.get("username");
		String password = (String) intent.get("password");
		String subject = (String) intent.get("subject");
		String data = (String) intent.get("message");

		params.put("application_key", application_key);
		params.put("msisdn", msisdn);
		params.put("username", username);
		params.put("password", password);
		params.put("subject", subject);
		params.put("message", data);

		/* OPTIONAL PARAMETERS */
		String thread_id =  (String)intent.get("thread_id");

		if (thread_id != null)
			params.put("thread_id", thread_id);

		final String method =  (String)intent.get("Action");
		String tag = Tag.MESSAGE_RECEIVED;
		
		Observer obs = new Observer()
		{
			public void postCompleted(String data) {
				// TODO Auto-generated method stub
				System.out.println("Developer Send returns result:"+ data);
				sendRequestEnded(method);
//				device_push_outbound_messages_list(PMMessageStore
//						.getInstance().getLastReceivedMessageId(), null, null, new Integer(20), null,
//						null, null);
				
			}

			public void postError(Exception e) {
			// request retry here
				System.out.println("DEVELOPER SEND UNSUCCESSFUL: " + e);
				
			}
			
		};

		System.out.println("Sending Request: handle_DEVELOPER_push_outbound_message_send");
		sendRequest(method, tag, params,obs);

		
	}

	/* NOTIFICATIONS */

	public static final int NOTIFICATION_ID = 1;

	

	/**
	 * Sends android notification via {@link NotificationManager}
	 * 
	 * @param context
	 * @param messages
	 */
//	private static void sendNotification(Context context, List<PMReceivedMessage> messages)
//	{
//		if (context == null || messages == null || messages.size() == 0)
//		{
//			return;
//		}
//
//		Context appContext = context.getApplicationContext();
//
//		NotificationManager mNotificationManager = (NotificationManager) appContext
//			.getSystemService(Context.NOTIFICATION_SERVICE);
//
//		Intent intent = new Intent(appContext, PMBaseActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext);
//		mBuilder.setSmallIcon(R.drawable.ic_launcher);
//
//		/* TEXT */
//		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//		mBuilder.setContentInfo(messages.size() + "");
//		mBuilder.setTicker(messages.get(0).getText());
//		if (messages.size() == 1)
//		{
//			/* SMALL */
//			mBuilder.setContentTitle(messages.get(0).getSubject());
//			mBuilder.setContentText(messages.get(0).getText());
//
//			/* LARGE */
//			inboxStyle.setBigContentTitle(messages.get(0).getSubject());
//			inboxStyle.addLine(messages.get(0).getText());
//
//			/* Intent */
//			intent.putExtra(PMBaseActivity.EXTRA_SHOW_THREAD, messages.get(0).getThreadID());
//			intent.putExtra(PMBaseActivity.EXTRA_SHOW_SUBJECT, messages.get(0).getSubject());
//
//		}
//		else
//		{
//			HashSet<String> uniqueSubjects = new HashSet<String>();
//			HashSet<Integer> uniqueThreads = new HashSet<Integer>();
//			for (PMReceivedMessage pmReceivedMessage : messages)
//			{
//				uniqueSubjects.add(pmReceivedMessage.getSubject());
//				uniqueThreads.add(pmReceivedMessage.getThreadID());
//
//				/* LARGE */
//				inboxStyle.addLine(pmReceivedMessage.getText());
//			}
//			String allSubjects = uniqueSubjects.toString();
//			allSubjects = allSubjects.substring(1, allSubjects.length() - 1);
//
//			/* SMALL */
//			mBuilder.setContentTitle(allSubjects);
//			mBuilder.setContentText(messages.size() + " new messages.");
//
//			/* LARGE */
//			inboxStyle.setBigContentTitle(allSubjects);
//			inboxStyle.setSummaryText(messages.size() + " new messages.");
//
//			/* Intent */
//			if (uniqueThreads.size() == 1)
//				intent.putExtra(PMBaseActivity.EXTRA_SHOW_THREAD, messages.get(0).getThreadID());
//			if (uniqueSubjects.size() == 1)
//				intent.putExtra(PMBaseActivity.EXTRA_SHOW_SUBJECT, messages.get(0).getSubject());
//		}
//
//		mBuilder.setStyle(inboxStyle);
//
//
//		/* INTENT */
//		PendingIntent contentIntent = PendingIntent.getActivity(appContext, 0, intent,
//			PendingIntent.FLAG_CANCEL_CURRENT);
//		mBuilder.setContentIntent(contentIntent);
//
//		/* SOUND */
//		mBuilder.setAutoCancel(true);
//		mBuilder.setOnlyAlertOnce(true);
//		//			Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		//			mBuilder.setSound(notificationSound);
//		mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//
//		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//	}

	private class WSResponse
	{
		public int result;
		public PMBaseResponse pmResponse;

		public WSResponse(int result, PMBaseResponse pmResponse)
		{
			this.result = result;
			this.pmResponse = pmResponse;
		}

		public WSResponse(int result)
		{
			this(result, null);
		}
	}

	/* Panacea API method names */
	public static class IntentAction
	{
		public static class WebService
		{
			//REGISTER
			public static final String DEVICE_CHECK_STATUS = "device_check_status";
			public static final String DEVICE_REGISTER = "device_register";
			public static final String DEVICE_REGISTER_MSISDN = "device_register_msisdn";
			public static final String DEVICE_REGISTER_VERIFICATION = "device_verification";
			public static final String DEVICE_UPDATE_PREFERENCES = "device_update_preferences";

			//LOCATION
			public static final String DEVICE_LOCATION_GET = "device_location_get";
			public static final String DEVICE_LOCATION_UPDATE = "device_location_update";

			//MESSAGE_RECEIVED
			public static final String DEVICE_PUSH_OUTBOUND_MESSAGE_LIST = "device_push_outbound_messages_list";
			public static final String DEVICE_PUSH_OUTBOUND_MESSAGE_UPDATE = "device_push_outbound_message_update";
			public static final String DEVICE_PUSH_OUTBOUND_MESSAGE_GET = "device_push_outbound_message_get";

			//MESSAGE_SENT
			public static final String DEVICE_PUSH_INBOUND_MESSAGE_LIST = "device_push_inbound_messages_list";
			public static final String DEVICE_PUSH_INBOUND_MESSAGE_SEND = "device_push_inbound_message_send";
			public static final String DEVICE_PUSH_INBOUND_MESSAGE_GET = "device_push_inbound_message_get";
		}

	}


	
	
	
	/**
	 * Defines result constants for the web service calls.
	 * 
	 * @see Result#OK
	 * @see Result#FAILURE_GENERAL
	 * @see Result#INVALID_METHOD
	 * @see Result#FAILURE_SERVER_UNAVAILABLE
	 */
	public static class Result
	{
		static final int OK = -1;
		static final int INVALID_METHOD = 0;
		static final int FAILURE_GENERAL = 1;
		static final int FAILURE_SERVER_UNAVAILABLE = 2;
	}





	public void onPreDataExecute(String tag) {
		// TODO Auto-generated method stub
		
	}

	public void onPostDataSuccess(String tag, String result, int status,
			String message, String method) {
		
		
	}

	public void onPostDataFailure(String tag, String result, int httpCode,
			String message, String method) {
		// TODO Auto-generated method stub
		
	}

	
}
