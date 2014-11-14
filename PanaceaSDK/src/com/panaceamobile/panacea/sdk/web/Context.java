package com.panaceamobile.panacea.sdk.web;

import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;

public interface Context {

	public static final int MODE_WORLD_READABLE = 0;
	
	/** 
	 * Get all files in the home dir of internal memory
	 * @return
	 */
	public Vector fileList();
	
	/**
	 * Get all files in the home dir subdirectory of internal memory
	 * @param directory
	 * @return
	 */
	public Vector fileList(String directory);
	
	/**
	 * Get the home dir path for internal memory
	 * @return
	 */
	public String getFilesDir();
	
	/**
	 * Open file in the home directory
	 * @param fileName
	 * @param overwrite
	 * @return
	 */
	public FileConnection openFile(String fileName, boolean overwrite);

	/**
	 * Open file in the home directory
	 * @param fileName
	 * @param overwrite
	 * @param create
	 * @return
	 */
	public FileConnection openFile(String fileName, boolean overwrite, boolean create);
	
	/**
	 * Does the given file exist in the home directory?
	 * @param path
	 * @return
	 */
	public boolean fileExists(String path);
	
	/**
	 * Read in all data for a file stored in the home directory in internal memory
	 * @param filename
	 * @return
	 */
	public byte[] readFileData(String filename);

	/**
	 * Delete a file|folder from the home folder
	 * @param filename
	 */
	public void deleteFile(String filename);

	/**
	 * Get all files in the home dir subdirectory of the given root
	 * @param root (if null, filename assumed to be absolute)
	 * @param directory
	 * @return
	 */
	public Vector fileList(String root, String directory);
	
	/**
	 * Get the home dir path for the given root
	 * @return
	 */
	public String getFilesDir(String root);
	
	/**
	 * Open file in the given root
	 * @param root (if null, filename assumed to be absolute)
	 * @param fileName
	 * @param overwrite
	 * @return
	 */
	public FileConnection openFile(String root, String filename, boolean overwrite);

	/**
	 * Open file in the given root
	 * @param root (if null, filename assumed to be absolute)
	 * @param fileName
	 * @param overwrite
	 * @param create
	 * @return
	 */
	public FileConnection openFile(String root, String filename, boolean overwrite, boolean create);
	
	/**
	 * Does the given file exist in the given root directory?
	 * @param root (if null, path assumed to be absolute)
	 * @param path
	 * @return
	 */
	public boolean fileExists(String root, String path);

	/**
	 * Read in all data for a file stored in the home directory in the given root
	 * @param root (if null, filename assumed to be absolute)
	 * @param filename
	 * @return
	 */
	public byte[] readFileData(String root, String filename);

	/**
	 * Returns list of all mounted roots
	 * @return
	 */
	public Vector mountedRoots();
	
	/** 
	 * Returns the memory card (SDCard) root 
	 * @return
	 */
	public String memoryCardRoot();
	
	/** 
	 * Returns the internal memory root
	 * @return
	 */
	public String internalRoot();
	
	/**
	 * Delete a file|folder from the the given root
	 * @param root (if null, filename assumed to be absolute)
	 * @param filename
	 */
	public void deleteFile(String root, String filename);

	/**
	 * Creates all folders in an absolute path
	 * @param path absolute path
	 */
	public void makePath(String path);

	/**
	 * Set the home directory
	 * @param homeDir
	 */
	public void setHomeDir(String homeDir);
	
	/**
	 * Read in all data from a resource 
	 * @param resName
	 * @return
	 */
	public byte[] readResourceData(String resName);
	
	/**
	 * Returns the default browser's User-Agent string 
	 * @return
	 */
	public String getUserAgentString();
	
	/**
	 * Open an internet connection
	 * @param location
	 * @param mode
	 * @param timeouts
	 * @return
	 */
	public HttpConnection openHttpConnection(String location, int mode, boolean timeouts);

	/**
	 * Open an internet connection
	 * @see ConnectionFactory
	 * @see TransportInfo
	 * @param location
	 * @param mode
	 * @param timeouts
	 * @param preferredConnections 
	 * @return
	 */
	public HttpConnection openHttpConnection(String location, int mode, boolean timeouts, int[] preferredConnections);

	/**
	 * Open an input stream to a resource
	 * @param resName
	 * @return
	 */
	public InputStream getResourceAsStream(String resName);

	
	public String getAppProperty(String propname);

	/**
	 * Returns the display width
	 * @return
	 */
	public int getDisplayWidth();
	/**
	 * Returns the display height
	 * @return
	 */
	public int getDisplayHeight();
	
	/**
	 * Open the default browser with the given url
	 * @param url
	 */
	public void openBrowser(String url);
	
	/**
	 * Compose an email
	 * @param addresses
	 * @param subject
	 * @param body
	 */
	public void initiateEmail(String [] addresses, String subject, String body);
	
	/**
	 * Dial out
	 * @param number
	 */
	public void initiateCall(String number);
	
	/**
	 * Returns the application name
	 * @return
	 */
	public String getAppName();
	
	/**
	 * Returns the application version
	 * @return
	 */
	public String getAppVersion();
	
	public String getDeviceId();
	public String getDeviceName();
	public String getDeviceManufacturer();
	public String getOsVersion();
	public String getPlatformVersion();
	public String getProcessor();
	
	public String getCurrentNetworkName();
	public String getCurrentNetworkType();	
	// In dBm
	public int getRadioSignalLevel();
	public String[] getNetworkNames();
	
	/**
	 * Returns the wifi mac address (if available)
	 * @return
	 */
	public String getWiFiMacAddress();
	
	// WLAN MAC
	public String getBSSID();
	
	/**
	 * Get the device IP address
	 */
	public String getIpAddress();		

	/**
	 * Get the device IP address list (where the device is capable of having more than one IP address)
	 */
	public Vector getIpAddresses();		

	/**
	 * Get the device IMSI (SIM)
	 */
	public String getIMSI();
	
	/**
	 * Get the device IMEI
	 */
	public String getIMEI();
			
	// VM STATS
	public static final int VM_RAM_STATS = 0;
	public static final int VM_CODE_STATS = 1;
	public static final int VM_TRANS_STATS = 2;
	public static final int VM_PERS_STATS = 3;
	public static final int VM_FLASH_STATS = 4;
	
	public static final int VM_STATS_FREE = 0;
	public static final int VM_STATS_ALLOC = 1;
	
	public int[][] getVmMemoryStats();
	
	/**
	 * Returns size of SDCard
	 * @return
	 */
	public int getTotalFlashSize();
	
	// INT STORAGE used / total	
	// EXT STORAGE used / total		
	/**/
	public static final int STORAGE_DEVICE = 0;
	public static final int STORAGE_SDCARD = 1;
	
	public static final int STORAGE_FREE = 0;
	public static final int STORAGE_TOTAL = 1;
	
	public long[][] getStorageStats();
	
	// BATTEY
	public int getBatteryLevel();
	public String getBatteryStatus();
			
	// APPLICATIONS
	public static final int APP_NAME 	= 0;
	public static final int APP_VERSION = 1;
	public static final int APP_MODULE 	= 2;
	public static final int APP_INDEX 	= 3;
	
	/**
	 * Get list of installed applications
	 * @return
	 */
	public Vector getInstalledApps();
	
	/**
	 * Get list of running applications
	 * @return
	 */
	public Vector getRunningApps();
	
	/**
	 * Get a full-sized screenshot, base64 encoded
	 * @return
	 */
	public String getScreenShot();
	
	/**
	 * Get a thumbnail-sized screenshot, base64 encoded
	 * @return
	 */
	public String getScreenShotThumbnail();
}