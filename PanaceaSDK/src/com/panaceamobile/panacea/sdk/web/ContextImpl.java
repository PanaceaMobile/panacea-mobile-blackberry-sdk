package com.panaceamobile.panacea.sdk.web;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.invoke.PhoneArguments;
import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventInjector;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.system.Memory;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCardException;
import net.rim.device.api.system.SIMCardInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.Keypad;

import com.panaceamobile.panacea.sdk.web.Base64;
import com.panaceamobile.panacea.sdk.web.Context;

public class ContextImpl implements Context {

	private static final String TAG = "ContextImlp";

	public static final String INTERNAL_ROOT = "file:///store/";
	public static final String HOME_DIR = "home/user/appdata/";
	
	private String appID = null;
	private String filesDir = null;
	private CodeModuleGroup appGroup = null;	
	private static ContextImpl _instance = null;
	
	public static ContextImpl getInstance() {		
		if (_instance == null)
			_instance = new ContextImpl();
		return _instance;
	}
	
	public ContextImpl() {		
		// load group to access app properties
		CodeModuleGroup[] allGroups = CodeModuleGroupManager.loadAll();
		String moduleName = ApplicationDescriptor.currentApplicationDescriptor().getModuleName();
		for (int i = 0; i < allGroups.length; i++) {
		   if (allGroups[i].containsModule(moduleName)) {
		      appGroup = allGroups[i];
		      break;
		    }
		}
		
		initData();	
		
		// remove all cache files
		// clearFiles();
	}
	
	private void initData() {		
		appID = Integer.toString(ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle());				
		filesDir = ApplicationDescriptor.currentApplicationDescriptor().getName().toLowerCase();
	}
		
	public void deleteFile(String filename) {
		deleteFile(null, getFilesDir() + filename);
	}
	
	/**
	 * Delete all files in the home directory of internal memory
	 */
	public void clearFiles() {
	//	UiLog.d(TAG, "Clearing all files!");
		
		Vector fileList = fileList();
		for (int i=0; i<fileList.size(); i++){
			String filename = (String) fileList.elementAt(i);
			if (filename.charAt(filename.length()-1) != '/')
				deleteFile(filename);
		}		
	}
	
	public void makePath(String path) {
		FileConnection fconn = null;
		try {
			fconn = (FileConnection) Connector.open(path);
		} catch (IOException ioe) {
	//		UiLog.e(TAG, "error in makePath() opening path "+path, ioe);
		}

		if (fconn == null || !fconn.exists()) {
			int lastSep = path.lastIndexOf('/', path.length()-2);			
			String smallerPath = path.substring(0, lastSep+1);
			
			// make smaller path
			makePath(smallerPath);
			
			//then append end dir
			try {
				fconn.mkdir();
			} catch (IOException e) {
	//			UiLog.e(TAG, "error making last part of path " + path, e);
			}
		}
		
		if (fconn != null) {
			try {
				fconn.close();
			} catch (IOException e) {
			}
		}
	}
	
	public Vector fileList(String directory) {
		return fileList(null, getFilesDir() + directory);
	}

	public Vector fileList() {
		return fileList("");
	}

	public String getFilesDir() {
		return getFilesDir(INTERNAL_ROOT);
	}

	public void setHomeDir(String homeDir) {
		filesDir = homeDir.toLowerCase();
	}
	
	public InputStream getResourceAsStream(String resName) {
		return getClass().getResourceAsStream(resName);
	}

	public String getUserAgentString() {
		String uas = System.getProperty("browser.useragent");
		if (uas == null || uas.length() == 0)
			uas = "mwcommon";
		return uas;
	}

	public boolean fileExists(String filename) {
		return fileExists(null, getFilesDir() + filename);
	}
	
	public boolean fileExists(String root, String filename) {
		boolean exists = false;
		FileConnection fconn = null;
		String fname = getRootPath(root, filename);
		try {
			fconn = (FileConnection) Connector.open(fname);
			exists = fconn.exists();
		} catch (Exception e) {
//			UiLog.d(TAG, "Error checking if file exists");
		} finally {
			if (fconn != null) {
				try {
					fconn.close();
				} catch (IOException e) {
				}
			}
		}
		return exists;
	}
	
	public FileConnection openFile(String filename, boolean overwrite) {
		return open(getFilesDir() + filename, overwrite, true);
	}

	/**
	 * Open file in home directory of internal memory
	 * @param filename
	 * @param overwrite
	 * @param create
	 * @return
	 */
	public FileConnection openFile(String filename, boolean overwrite, boolean create) {
		return open(getFilesDir() + filename, overwrite, create);
	}

	/**
	 * Open a file 
	 * @param filename absolute path to file
	 * @param overwrite
	 * @param create
	 * @return
	 */
	private FileConnection open(String filename, boolean overwrite, boolean create) {
		FileConnection fconn = null;
		try {
    //   	 	UiLog.d(TAG, "opening file: "+filename);
			fconn = (FileConnection) Connector.open(filename);
			if (!fconn.exists()) {
				if (create) {
	   //    	 		UiLog.d(TAG, "File not found, creating...");
					fconn.create();
				} else {
					// doesn't exist and told not to create, so return null
	   //    	 		UiLog.d(TAG, "File not found, returning null...");
					try {
						fconn.close();
					} catch (IOException ioe) {}
					return null;
				}
			} else if (overwrite) {
	//       	 	UiLog.d(TAG, "Existing file found, recreating...");
				// recreate file
	       	 	fconn.truncate(0);
			}
			return fconn;
		} catch (Exception e) {
	//		UiLog.e(TAG, "error opening file " + filename, e);
			e.printStackTrace();
			if (fconn != null) {
				try {
					fconn.close();
				} catch (IOException ioe) {}				
			}				
		}
		return null;
	}
	
	public byte[] readFileData(String filename) {
		return readFileData(null, getFilesDir()+filename);
	}
	
	public byte[] readResourceData(String resName) {
		InputStream in = getResourceAsStream(resName);
		if (in == null)
			return null;
		
		byte [] data = null;
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(in);
			
			int read = 0;					
			data = new byte[ (int) dis.available() ];
			read = dis.read(data);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {												
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();				
			}
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}	
	
	public HttpConnection openHttpConnection(String location, int mode,	boolean timeouts) {
		ConnectionManager connectionManager = ConnectionManager.getInstance();
		HttpConnection connection = null;
		try {
			connection = (HttpConnection) connectionManager.open(location, mode, timeouts);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public HttpConnection openHttpConnection(String location, int mode, boolean timeouts, int[] preferredConnections) {
		ConnectionManager connectionManager = ConnectionManager.getInstance();
		HttpConnection connection = null;
		try {
			connection = (HttpConnection) connectionManager.open(location, mode, timeouts, preferredConnections);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;		
	}

	public String getAppProperty(String propname) {
		// Get the property
		String prop = appGroup.getProperty(propname);		
		return prop;
	}

	public int getDisplayWidth() {
		return Display.getWidth();
	}
	
	public int getDisplayHeight() {
		return Display.getHeight();
	}	
	
	public void openBrowser(String url) {
		Browser.getDefaultSession().displayPage(url);		
	}
	
	public void initiateEmail(String [] addresses, String subject, String body) {
		Message m = new Message();
		try {
			Address[] bbAddresses = new Address[ addresses.length];
			for (int i=0; i<addresses.length; i++) {
					bbAddresses[i] = new Address(addresses[i], "");
			}		
				m.addRecipients(net.rim.blackberry.api.mail.Message.RecipientType.TO, bbAddresses);
			m.setContent(body);
			m.setSubject(subject);				
		} catch (MessagingException e) {
			//UiLog.e(TAG, "Error confirguring email.", e);
			return;
		}		
		Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, new MessageArguments(m));		
	}
	
	public void initiateCall(String number) {
		PhoneArguments call = new PhoneArguments(PhoneArguments.ARG_CALL, number);
		Invoke.invokeApplication(Invoke.APP_TYPE_PHONE, call);		
	}

	public String getAppName() {
		return ApplicationDescriptor.currentApplicationDescriptor().getName(); 
	}
	
	public String getAppVersion() {
		return ApplicationDescriptor.currentApplicationDescriptor().getVersion();		
	}
	
	// Device data
	
	public String getDeviceId() {
		return Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
	}
	
	public String getDeviceName() {
		return DeviceInfo.getDeviceName();
	}
	
	public String getDeviceManufacturer() {
		return DeviceInfo.getManufacturerName();
	}
	
	public String getOsVersion() {
		return DeviceInfo.getSoftwareVersion();
	}
	
	public String getPlatformVersion() {
		return DeviceInfo.getPlatformVersion();
	}
	
	public String getProcessor() {
		return "<unavailable>";
	}
	
	public String getCurrentNetworkName() {
		return RadioInfo.getCurrentNetworkName();
	}
	
	public String getCurrentNetworkType() {
		int nt = RadioInfo.getNetworkType();
		String nts = "";
		switch (nt) {
			case RadioInfo.NETWORK_802_11: nts = "802.11"; break;
			case RadioInfo.NETWORK_GPRS: nts = "GPRS"; break;
			case RadioInfo.NETWORK_CDMA: nts = "CDMA"; break;
		}		
		return nts;
	}
	
	// In dBm
	public int getRadioSignalLevel() {
		return RadioInfo.getSignalLevel();
	}
	
	public String[] getNetworkNames() {
		int nn = RadioInfo.getNumberOfNetworks();
		String [] names = new String[nn];
		for (int i=0; i<nn; i++) {
			names[i] = RadioInfo.getNetworkName(i);
		}		
		return names;
	}
	
	// WiFi MAC address
	// NOT AVAILABLE ???
	public String getWiFiMacAddress() {
		return "<unavailable>";
	}
	
	// WLAN MAC
	public String getBSSID() {
		if (!DeviceInfo.isSimulator())
			return WLANInfo.getAPInfo().getBSSID();
		return "<simulator>";
	}
	
	// IP address
	public String getIpAddress() {			
		int apnId = 0;
		try {
			apnId = RadioInfo.getAccessPointNumber("MagicRudyAPN.rim");
		} catch (RadioException e) {
	//		Log.e(TAG, "Failed to retreive IP address", e);
			return "<unknown>";
		}
		byte[] ipByte = RadioInfo.getIPAddress(apnId);
		String ip = "";
		for (int i = 0; i < ipByte.length; i++) {
		    int temp = (ipByte[i] & 0xff);
		    if (i < 3)
		    	ip = ip.concat("" + temp + ".");
		    else {
		    	ip = ip.concat("" + temp);
		    }
		}	
		return ip;
	}

	public Vector getIpAddresses() {
		Vector ips = new Vector();
		ips.addElement(getIpAddress());
		return ips;
	}

	// IMSI
	
	public String getIMSI() {
		try {
			return GPRSInfo.imeiToString(SIMCardInfo.getIMSI(), false);
		} catch (SIMCardException e) {
	//		Log.e(TAG, "Failed to retreive IMSI", e);
			return "<unavailable>";
		}		
	}
	
	public String getIMEI() {
		return GPRSInfo.imeiToString(GPRSInfo.getIMEI());		
	}
			
	// VM STATS
	public int[][] getVmMemoryStats() {
		int [][] stats = new int[VM_FLASH_STATS+1][2];
		
		// VM RAM
		stats[VM_RAM_STATS][VM_STATS_FREE] = Memory.getRAMStats().getFree();
		stats[VM_RAM_STATS][VM_STATS_ALLOC] = Memory.getRAMStats().getAllocated();		
		// VM Code
		stats[VM_CODE_STATS][VM_STATS_FREE] = Memory.getCodeStats().getFree();
		stats[VM_CODE_STATS][VM_STATS_ALLOC] = Memory.getCodeStats().getAllocated();
		// VM Trans
		stats[VM_TRANS_STATS][VM_STATS_FREE] = Memory.getTransientStats().getFree();
		stats[VM_TRANS_STATS][VM_STATS_ALLOC] = Memory.getTransientStats().getAllocated();
		// VM Pers
		stats[VM_PERS_STATS][VM_STATS_FREE] = Memory.getPersistentStats().getFree();
		stats[VM_PERS_STATS][VM_STATS_ALLOC] = Memory.getPersistentStats().getAllocated();
		// VM Flash
		stats[VM_FLASH_STATS][VM_STATS_FREE] = Memory.getFlashStats().getFree();
		stats[VM_FLASH_STATS][VM_STATS_ALLOC] = Memory.getFlashStats().getAllocated();
		
		return stats;
	}
	
	/**
	 * Returns the total flash size (internal)
	 */
	public int getTotalFlashSize() {
		return DeviceInfo.getTotalFlashSize(); // getTotalFlashSizeEx() from 6.0
	}
	
	// INT STORAGE used / total	
	// EXT STORAGE used / total		
	public long[][] getStorageStats() {
		
		Enumeration rootEnum = FileSystemRegistry.listRoots();
		long [][] storage = new long[STORAGE_SDCARD+1][2];
		
		while (rootEnum.hasMoreElements()) {
	      String root = (String) rootEnum.nextElement();
	      FileConnection fc = null;
			try {
				fc = (FileConnection) Connector.open("file:///" + root);
				if (root.equalsIgnoreCase("store/")) {
					storage[STORAGE_DEVICE][STORAGE_FREE] = fc.availableSize();
					storage[STORAGE_DEVICE][STORAGE_TOTAL] = fc.totalSize();					
				}
				else if (root.equalsIgnoreCase("sdcard/")) {
					storage[STORAGE_SDCARD][STORAGE_FREE] = fc.availableSize();
					storage[STORAGE_SDCARD][STORAGE_TOTAL] = fc.totalSize();
				}
				fc.close();
			} catch (Exception e) {
	//			Log.e(TAG, "Invalid root value while determining storage.", e);
			}			
	   } 	
		return storage;
	}
	
	// BATTEY LEVEL
	public int getBatteryLevel() {
		return DeviceInfo.getBatteryLevel();
	}
	
	// BATTERY STATUS
	public String getBatteryStatus() {
		int bs = DeviceInfo.getBatteryStatus();
		StringBuffer bss = new StringBuffer();
		String appender = "";
		if ((bs & DeviceInfo.BSTAT_CHARGING) > 0 || DeviceInfo.getBatteryLevel() == 100) { 
			bss.append("Charging");
			appender = ", ";
		}
		else if ((bs & DeviceInfo.BSTAT_LOW_RATE_CHARGING) > 0 ) {
			bss.append(appender).append("Low Charging");
			appender = ", ";
		}
		else {
			bss.append("Not Charging");
			appender = ", ";			
		}		
		if ((bs & DeviceInfo.BSTAT_LOW) > 0 ) {
			bss.append(appender).append("Low");
			appender = ", ";
		}
		if ((bs & DeviceInfo.BSTAT_TOO_COLD) > 0 ) {
			bss.append(appender).append("COLD");
			appender = ", ";
		}
		if ((bs & DeviceInfo.BSTAT_TOO_HOT) > 0 ) {
			bss.append(appender).append("HOT");
			appender = ", ";
		}
		return bss.toString();
	}
				
	// APPLICATIONS
	public Vector getInstalledApps() {
		return ApplicationUtils.getInstalledApps();
	}
	
	// RUNNING APPLICATIONS
	public Vector getRunningApps() {
		return ApplicationUtils.getRunningApps();
	}

	public void installResetAssistant() {
	
        try {
            byte[] codData = readResourceData("/cod/ResetAssistant.cod");        
			int dummycodhandle = CodeModuleManager.createNewModule(codData.length, codData, codData.length);
			   
			//Save the module
			int result = CodeModuleManager.saveNewModule(dummycodhandle, true);
			if (result != CodeModuleManager.CMM_OK && result != CodeModuleManager.CMM_OK_MODULE_OVERWRITTEN)
			{
				//The cod file was not saved.
				throw new Exception("Failed to save dummy cod.");
			}
			                
			int dummycodhandle2=CodeModuleManager.getModuleHandle("ResetAssistant");
			ApplicationDescriptor[] desc = CodeModuleManager.getApplicationDescriptors(dummycodhandle2);			
			ApplicationManager.getApplicationManager().runApplication(desc[0], false);
        }
		catch (Exception e) {
			e.printStackTrace();
		}						
	}
	
	private void resetInstallDelete() {
        try {
            byte[] codData = readResourceData("/cod/ResetAssistant.cod");        
			int dummycodhandle = CodeModuleManager.createNewModule(codData.length, codData, codData.length);
			   
			//Save the module
			int result = CodeModuleManager.saveNewModule(dummycodhandle, true);
			if (result != CodeModuleManager.CMM_OK && result != CodeModuleManager.CMM_OK_MODULE_OVERWRITTEN)
			{
				//The cod file was not saved.
				throw new Exception("Failed to save dummy cod.");
			}
			                
			int dummycodhandle2=CodeModuleManager.getModuleHandle("ResetAssistant");
			ApplicationDescriptor[] desc = CodeModuleManager.getApplicationDescriptors(dummycodhandle2);			
			ApplicationManager.getApplicationManager().runApplication(desc[0], false);
			
			// inject key for trusted app status: up*1, followed by ENTER
			InputSimUtils.keyInjectDelayed(1, 0, -1, true, 5*1000);    				
			
			// Make sure the application is already started, assume 5 seconds is enough      
			Thread.sleep(5000);			    
			CodeModuleManager.deleteModuleEx(dummycodhandle2,true);
			CodeModuleManager.promptForResetIfRequired();
			Thread.sleep(5000);			    			
		}
		catch (Exception e) {
			e.printStackTrace();
		}				
	}
	
	public void softReset9500() {	
		InputSimUtils.keyInject(5, 0, 1, true);
	}

	public void softReset9900() {
		InputSimUtils.keyInject(1, 0, 1, true);
	}
	
	public void softResetOS5() {
		InputSimUtils.keyInject(1, 0, 25, false);
		InputSimUtils.keyInject(1, 0, -1, true);		
	}

	public void softResetKeys() {
		String device = DeviceInfo.getDeviceName();
		String os = getOsVersion();
		if (device.startsWith("95")) {
			softReset9500();
		} else if (device.startsWith("99") || device.startsWith("98")) {
			softReset9900();
		} else if (os.startsWith("5")) {
			softResetOS5();
		} else {
			softReset9900();
		}
	}
	
	public void softReset() {
		resetInstallDelete();
		softResetKeys();
	}
	
	public void softReset1() {
		// move to home screen
		ApplicationManager.getApplicationManager().requestForegroundForConsole();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Hold down Alt-RShift, press Del	
		EventInjector.KeyCodeEvent delEventDown = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_DOWN, 
				(char) Keypad.KEY_DELETE, 
				KeypadListener.STATUS_ALT | KeypadListener.STATUS_SHIFT_RIGHT);
		
		EventInjector.KeyCodeEvent delEventUp = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_UP, 
				(char) Keypad.KEY_DELETE, 
				KeypadListener.STATUS_ALT | KeypadListener.STATUS_SHIFT_RIGHT);
		
		EventInjector.invokeEvent(delEventDown);
		EventInjector.invokeEvent(delEventUp);		
	}
	
	public void softReset2() {
		// move to home screen
		ApplicationManager.getApplicationManager().requestForegroundForConsole();
		
		boolean ready = false;
		while (!ready) {
			int fgpid = ApplicationManager.getApplicationManager().getForegroundProcessId();
			int curpid = ApplicationManager.getApplicationManager().getProcessId(
						ApplicationDescriptor.currentApplicationDescriptor()
					);
			ready = (fgpid != curpid);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Hold down Alt-RShift, press Del	
		EventInjector.KeyCodeEvent altEventDown = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_DOWN, 
				(char) Keypad.KEY_ALT, 
				0);

		EventInjector.KeyCodeEvent altEventUp = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_UP, 
				(char) Keypad.KEY_ALT, 
				0);
		
		EventInjector.KeyCodeEvent rshiftEventDown = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_DOWN, 
				(char) Keypad.KEY_SHIFT_RIGHT, 
				0);

		EventInjector.KeyCodeEvent rshiftEventUp = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_UP, 
				(char) Keypad.KEY_SHIFT_RIGHT, 
				0);
		
		EventInjector.KeyCodeEvent delEventDown = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_DOWN, 
				(char) Keypad.KEY_DELETE, 
				0);
		
		EventInjector.KeyCodeEvent delEventUp = new EventInjector.KeyCodeEvent (
				EventInjector.KeyCodeEvent.KEY_UP, 
				(char) Keypad.KEY_DELETE, 
				0);
		
		EventInjector.invokeEvent(altEventDown);
		EventInjector.invokeEvent(rshiftEventDown);
		EventInjector.invokeEvent(delEventDown);
		EventInjector.invokeEvent(delEventUp);		
		EventInjector.invokeEvent(altEventUp);		
		EventInjector.invokeEvent(rshiftEventUp);		
	}
	
	/**
	 * Captures a screenshot bitmap, converts to jpg and then base64 encodes the raw data.
	 */
	public String getScreenShot() {
		String screenshot = "";
		try {
			Bitmap bm = new Bitmap(Display.getWidth(), Display.getHeight());
			Display.screenshot(bm);
			JPEGEncodedImage jpg = JPEGEncodedImage.encode(bm, 80);
			byte[] data = jpg.getData();
			screenshot = Base64.encode(data);
		} catch (Exception e) {
	//		UiLog.e(TAG, "Error getting screenshot: ", e);
		}

		return screenshot;
	}
	
	/**
	 * Captures a screenshot thumbnail (1/4 size), converts to jpg and then base64 encodes the raw data.
	 */
	public String getScreenShotThumbnail() {
		String screenshot = "";
		try {
			Bitmap bm = new Bitmap(Display.getWidth(), Display.getHeight());
			Display.screenshot(bm);
			Bitmap th = new Bitmap(Display.getWidth()>>1, Display.getHeight()>>1);
			bm.scaleInto(th, Bitmap.FILTER_LANCZOS);
			JPEGEncodedImage jpg = JPEGEncodedImage.encode(th, 80);
			byte[] data = jpg.getData();
			screenshot = Base64.encode(data);
		} catch (Exception e) {
	//		UiLog.e(TAG, "Error getting screenshot: ", e);
		}

		return screenshot;
	}

	private String getRootPath(String root, String path) {
		String dir = path;
		if (root != null) {
			dir = getFilesDir(root) + path;
		}
		return dir;
	}

	public Vector fileList(String root, String directory) {
		return fileList( root, directory, false );
	}

	public Vector fileList(String root, String directory, boolean subDirs ) {
		Vector fileList = new Vector();
		String dir = getRootPath(root, directory);
		FileConnection rootConn = null;
		try {
			rootConn = (FileConnection) Connector.open(dir);
			if (!rootConn.exists()) {
				makePath(dir);
			} else {
				Enumeration fileEnum = rootConn.list();				
				while (fileEnum.hasMoreElements()) {
					String filename = (String) fileEnum.nextElement();
					// ignore sub-directories
					if (subDirs || filename.charAt(filename.length()-1) != '/') {
						fileList.addElement(filename);
					}
				}
			}
		} catch (Exception e) {
	//		UiLog.e("ContextImpl.fileList()", "Failure to list files.");
		} finally {
			try {
				rootConn.close();						
			} catch (Exception e) {				
			}
		}
		
		return fileList;
	}

	public String getFilesDir(String root) {
		String dir = root + HOME_DIR + filesDir + "/";
		if (!fileExists(null, dir)) {
			makePath(dir);
			FileConnection fc = null;
			try {
				fc = openFile(dir, false, false);
				//fc.setHidden(true);
			} catch (Exception e) {
			} finally {
				try {
					if (fc != null)
						fc.close();
				} catch (Exception e) {
				}
			}
		}
		return dir;
	}

	public FileConnection openFile(String root, String filename, boolean overwrite) {
		return openFile(root, filename, overwrite, true);
	}

	public FileConnection openFile(String root, String filename, boolean overwrite, boolean create) {
		String fname = getRootPath(root, filename);
		return open(fname, overwrite, create);
	}
	
	public byte[] readFileData(String root, String filename) {
		String fname = getRootPath(root, filename);
		
		FileConnection fconn = open(fname, false, false);
		if (fconn == null)
			return null;		
		
		byte [] data = null;
		DataInputStream dis = null;
		try {
			dis = fconn.openDataInputStream();
			
			int read = 0;					
			data = new byte[ (int) fconn.fileSize() ];
			read = dis.read(data);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {												
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();				
			}
			try {
				fconn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public Vector mountedRoots() {
		Vector roots = new Vector();
		Enumeration e = FileSystemRegistry.listRoots();
	    while (e.hasMoreElements()) {
	    	roots.addElement("file:///" + e.nextElement());
	    }
	    return roots;
	}

	public String memoryCardRoot() {
		Vector roots = mountedRoots();
		for (int i=0; i<roots.size(); i++) {
			String root = (String) roots.elementAt(i);
			if (root.indexOf("SDCard") >= 0)
				return root;
			if (root.indexOf("CFCard") >= 0)
				return root;
			if (root.indexOf("MemoryStick") >= 0)
				return root;
		}
		return null;
	}

	public String internalRoot() {
		return INTERNAL_ROOT;
	}

	public void deleteFolder(String folder) {
		deleteFolder( folder, true );
	}
	
	public void deleteFolder(String folder, boolean recurse ) {
		Vector files = fileList(null, folder);
		for (int i=0; i<files.size();i++) {
			String file = folder + (String) files.elementAt(i);
			if (file.endsWith("/") && recurse) {
				deleteFolder(file);
			} else {
				deleteFile(null, file);
			}
		}
	}
	
	public void deleteFile(String root, String filename) {
		String dfile = filename;
		if (root != null) {
			dfile = getFilesDir(root) + filename;
		}

		if (dfile.endsWith("/")) {
			deleteFolder(dfile);
		}
		
		FileConnection fconn = null;
		try {
			fconn = (FileConnection) Connector.open(dfile);
			if (fconn != null && fconn.exists()) {
				fconn.delete();
			}
		} catch (Exception e) {
//			UiLog.e(TAG, "Error deleting file: " + filename, e);
		} finally {
			try {
				if (fconn != null)
					fconn.close();
			} catch (IOException e) {
			}
		}
	}


}
