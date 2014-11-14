package com.panaceamobile.panacea.sdk;

import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.bluetooth.UUID;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.ContentProtectedHashtable;

import org.json.me2.JSONArray;


public class PMPreferencesHelper {

	
	//keys
	private static final String APPLICATION_KEY = "application_key";
	private static final String DEVICE_SIGNATURE = "device_signature";
	private static final String CHANNEL_KEY = "channel_key";
	private static final String VERIFIED = "verified";
	private static final String PHONE_NUMBER = "phone_number";
	private static final String PUSH_CHANNEL_URL = "push_channel_url";
	private static final String PUSH_CHANNEL_ID = "push_channel_id";
	private static final String UNIQUE_DEVICE_ID = "unique_device_id";
	private static final String DEVICE_MANUFACTURER = "device_manufacturer";
	private static final String DEVICE_MODEL = "device_model";
	private static final String OPERATING_SYSTEM = "operating_system";
	private static final String OPERATING_SYSTEM_VERSION = "operating_system_version";
	private static final String TIMEZONE = "timezone";
	private static final String LOCALE = "locale";
	private static final String EVENT_KEY = "EVENT_KEY";
	private static final String APPLICATION_NAME = "application_name";
	private static final String GIVEN_NAME = "given_name";
	
	//values
	private static final String OPERATING_SYSTEM_VALUE = "Blackberry";
	private static final String DEVICE_MANUFACTURER_VALUE = "Blackberry";
	
	
	
	
	
	public static final int DEFAULT_POLL_INTERVAL_SECS = 600; // 10 mins

	// com.panasea.sdk
	private static final long PREFERENCES_KEY = 0xc24f50a2641188caL;
	public static void setEventKey(long eventKey){
		setSetting(EVENT_KEY,String.valueOf(eventKey));
	}
	
	public static long getEventKey()
	{
		String longAsString = getSetting(EVENT_KEY,"-1");
		long l = Long.parseLong(longAsString);
		return l;
	}
	
	public static String getGivenName()
	{
		return getSetting(GIVEN_NAME,String.valueOf(DeviceInfo.getDeviceId()));
	}
	
	public static void setGivenName(String name)
	{
		setSetting(GIVEN_NAME,name);
	}
	public static String getApplicationName()
	{
		return getSetting(APPLICATION_NAME,null);
	}
	public static void setApplicationName(String name)
	{
		setSetting(APPLICATION_NAME,name);
	}
	public static void setSetting(String key, String value) {
		if (key == null)
			return;

		PersistentObject store = PersistentStore
				.getPersistentObject(PREFERENCES_KEY);
		ContentProtectedHashtable settings = (ContentProtectedHashtable) store
				.getContents();
		if (settings == null) {
			settings = new ContentProtectedHashtable();
			store.setContents(settings);
		}
		if (value != null)
			settings.put(key, value);
		else
			settings.remove(key);
		store.commit();
	}
	public static void purgeSettings()
	{
		PersistentObject store = PersistentStore
				.getPersistentObject(PREFERENCES_KEY);
		store.setContents(new ContentProtectedHashtable());
		store.commit();
	}
	public static String getSetting(String key, String defaultValue) {
		try {
			PersistentObject store = PersistentStore
					.getPersistentObject(PREFERENCES_KEY);
			ContentProtectedHashtable settings = (ContentProtectedHashtable) store
					.getContents();

			if (settings == null) {
				settings = new ContentProtectedHashtable();
				store.setContents(settings);
				store.commit();
			}
			if (settings.containsKey(key))
				return (String) settings.get(key);
		} catch (Exception e) {
			return defaultValue;
		}

		return defaultValue;
	}

	/**
	 * Delete all settings
	 */
	public static void clearAllSettings() {
		
		//before we clear the settings there are several things we need to keep
		String appkey = getApplicationKey();
		String uuid = getUniqueDeviceId();
		String pushkey = getPushNotificationKey();
		
		PersistentObject store = PersistentStore
				.getPersistentObject(PREFERENCES_KEY);
		
		
		ContentProtectedHashtable settings = new ContentProtectedHashtable();
		store.setContents(settings);
		store.commit();
		
		setApplicationKey(appkey);
		setSetting(UNIQUE_DEVICE_ID,uuid);
		setPushNotificationKey(pushkey);
	}
	/**
	 * Saves the current device configuration to SharedPreferences
	 * 
	 * @param context
	 */
	public static void setDeviceConfiguration()
	{
		String unique_device_id = getUniqueDeviceId();
		String device_manufacturer = getDeviceManufacturer();
		String device_model = getDeviceModel();
		String operating_system = getOpereatingSystem();
		String operating_system_version = getOperatingSystemVersion();
		String timezone = getTimezone();
		String locale = getLocale();
		String push_channel_id = getPushChannelId();

		setSetting(UNIQUE_DEVICE_ID, unique_device_id);
		setSetting(DEVICE_MANUFACTURER, device_manufacturer);
		setSetting(DEVICE_MODEL, device_model);
		setSetting(OPERATING_SYSTEM, operating_system);
		setSetting(OPERATING_SYSTEM_VERSION, operating_system_version);
		setSetting(TIMEZONE, timezone);
		setSetting(LOCALE, locale);
		setSetting(PUSH_CHANNEL_ID, push_channel_id);
	}
	
	public static String getPushServerUrl()
	{
		return getSetting(PUSH_CHANNEL_URL, null);
	}
	
	public static void setPushServerUrl(String url)
	{
		setSetting(PUSH_CHANNEL_URL, url);
	}
	
	
	public static void setBoolean(String key, boolean value) {
		String sval = value ? "1" : "0";
		PMPreferencesHelper.setSetting(key, sval);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		String sval = PMPreferencesHelper.getSetting(key, null);
		if (sval != null) {
			return sval.equals("1");
		}
		return defaultValue;
	}
	


	public static String getOpereatingSystem()
	{
		return OPERATING_SYSTEM_VALUE;
	}

	/**
	 * Saves application key to SharedPreferences
	 * 
	 * @param applicationKey
	 * @param context
	 */
	public static void setApplicationKey(String applicationKey)
	{
		setSetting(APPLICATION_KEY, applicationKey);
	}

	/**
	 * Retrieves the application key from SharedPreferences
	 * 
	 * @param context
	 * @return Application Key
	 */
	public static String getApplicationKey()
	{
		return getSetting(APPLICATION_KEY,null);
	}

	/**
	 * Saves Device Signature to SharedPreferences
	 * 
	 * @param deviceSignature
	 * @param context
	 */
	public static void setDeviceSignature(String deviceSignature)
	{
		setSetting(DEVICE_SIGNATURE, deviceSignature);
	}

	/**
	 * Retrieves the Device Signature from SharedPreferences
	 * 
	 * @param context
	 * @return Device Signature
	 */
	//5bd91185e0e824726bad68f2a6cbdf83
	public static String getDeviceSignature()
	{
		String test = getSetting(DEVICE_SIGNATURE,null);
		return getSetting(DEVICE_SIGNATURE,null);
	}

	/**
	 * Saves Push Notification Key to SharedPreferences
	 * 
	 * @param key
	 * @param context
	 */
	public static void setPushNotificationKey(String key)
	{
		setSetting(CHANNEL_KEY, key);
	}

	/**
	 * Retrieves the Push Notification Key from SharedPreferences
	 * 
	 * @param context
	 * @return Push Notification Key
	 */
	public static String getPushNotificationKey()
	{
		return getSetting(CHANNEL_KEY,null);
	}

	/**
	 * Checks if the device is verified and saved in SharedPreferences
	 * 
	 * @param context
	 * @return true if it is verified otherwise false
	 */
	public static boolean isVerified()
	{
		return getBoolean("verified", false);
	}

	/**
	 * Saves a Verified boolean to SharedPreferences
	 * 
	 * @param verified
	 * @param context
	 */
	public static void setVerified(boolean verified)
	{
		setBoolean(VERIFIED, verified);
	}

	/**
	 * Saves phone number to SharedPreferences
	 * 
	 * @param phoneNumber
	 * @param context
	 */
	public static void setPhoneNumber(String phoneNumber)
	{
		setSetting(PHONE_NUMBER, phoneNumber);
	}

	/**
	 * Retrieves the Phone Number from SharedPreferences
	 * 
	 * @param context
	 * @return Phone Number
	 */
	public static String getPhoneNumber()
	{
		return getSetting(PHONE_NUMBER,null);
	}
	
	
	
	
	
	
	/**
	 * Generates a UUID
	 * 
	 * @param context
	 * @return the generated or saved UUID
	 */
	public static String getUniqueDeviceId()
	{
		//get existing id
		String uid = getSetting(UNIQUE_DEVICE_ID,null);

		//if it does not exist
		if (uid == null)
		{        
			uid = generateUniqueDeviceId();	
			setSetting(UNIQUE_DEVICE_ID,uid);	
		}
		return uid;
	}

	
	private static String generateUniqueDeviceId()
	{
		  int deviceID = DeviceInfo.getDeviceId();
	        UUID u = new UUID(deviceID);
	        String uuid= u.toString();

	   
	        
	        
	        return uuid
	        +"-"+Integer.toString(UIDGenerator.getUID()).substring(4,8)
	        +"-"+Integer.toString(UIDGenerator.getUID()).substring(4,8)
	        +"-"+Integer.toString(UIDGenerator.getUID()).substring(3,7)
	        +"-"+Long.toString(System.currentTimeMillis()).substring(0,12); 

	}

	/**
	 * Gets device Manufacturer
	 * 
	 * @return string of Device Manufacturer
	 */
	public static String getDeviceManufacturer()
	{
		return DEVICE_MANUFACTURER_VALUE;
	}

	/**
	 * Gets device Model
	 * 
	 * @return string of Device Model
	 */
	public static String getDeviceModel()
	{
		return DeviceInfo.getDeviceName();
	}

	/**
	 * Gets Operating System Version
	 * 
	 * @return string of Operating System Version
	 */
	public static String getOperatingSystemVersion()
	{
		return DeviceInfo.getSoftwareVersion();
	}

	/**
	 * Gets current Time zone
	 * 
	 * @return string of current Time zone
	 */
	public static String getTimezone()
	{
		TimeZone tz = TimeZone.getDefault();
		//String timezone = tz.getDisplayName(false, TimeZone.SHORT) + " Timezone id :: " + tz.getID();
		String timezone = tz.getID();
		return timezone;
	}

	/**
	 * Gets current Locale
	 * 
	 * @return string of current Locale
	 */
	public static String getLocale()
	{
		String locale = Locale.getDefault().toString();
		return locale;
	}

	
	/**
	 * Checks if there are any differences in current configuration and saved
	 * configuration
	 * 
	 * @return true if there are differences, otherwise false
	 */
	public static boolean hasDeviceConfigurationChanged()
	{
		String unique_device_id = getUniqueDeviceId();
		String device_manufacturer = getDeviceManufacturer();
		String device_model = getDeviceModel();
		String operating_system = getOpereatingSystem();
		String operating_system_version = getOperatingSystemVersion();
		String timezone = getTimezone();
		String locale = getLocale();
		String push_channel_id = getPushChannelId();

		if (!unique_device_id.equals(getSetting(UNIQUE_DEVICE_ID, null)))
		{
			return true;
		}

		if (!device_manufacturer.equals(getSetting(DEVICE_MANUFACTURER, null)))
		{
			return true;
		}

		if (!device_model.equals(getSetting(DEVICE_MODEL, null)))
		{
			return true;
		}

		if (!operating_system.equals(getSetting(OPERATING_SYSTEM, null)))
		{
			return true;
		}

		if (!operating_system_version.equals(getSetting(OPERATING_SYSTEM_VERSION, null)))
		{
			return true;
		}

		if (!timezone.equals(getSetting(TIMEZONE, null)))
		{
			return true;
		}

		if (!locale.equals(getSetting(LOCALE, null)))
		{
			return true;
		}

		if (!push_channel_id.equals(getSetting(PUSH_CHANNEL_ID, null)))
		{
			return true;
		}


		return false;
	}

	public static String getPushChannelId() {
		return "4";
	}
	
	
	
}
