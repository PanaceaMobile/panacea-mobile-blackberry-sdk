package com.panaceamobile.panacea.sdk.web;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.CodeModuleManager;


public class ApplicationUtils {
	
	public static final String TAG = "ApplicationUtils";
	// APPLICATIONS
	public static Vector getInstalledApps() {
		return getApps(false);
	}

	// APPLICATIONS
	public static Vector getAllApps() {
		return getApps(true);
	}

	// APPLICATIONS
	private static Vector getApps(boolean all) {
		Vector apps = new Vector();
		int [] modHandles = CodeModuleManager.getModuleHandles(false);		
		for ( int i=0; i<modHandles.length; i++ ) {
			boolean isLibrary = CodeModuleManager.isLibrary( modHandles[i] );
			boolean isInternal = (!all && CodeModuleManager.getModuleName(modHandles[i]).startsWith("net_rim"));
			if ( !isLibrary && !isInternal) {
				ApplicationDescriptor descriptors[] = CodeModuleManager.getApplicationDescriptors(modHandles[i]);
				if ( descriptors != null )
					for ( int j=0; j<descriptors.length; j++ ) {
						String [] appNameVersion = new String[4];
						appNameVersion[Context.APP_NAME] = descriptors[j].getName();
						appNameVersion[Context.APP_VERSION] = descriptors[j].getVersion();
						appNameVersion[Context.APP_MODULE] = descriptors[j].getModuleName();
						appNameVersion[Context.APP_INDEX] = String.valueOf(descriptors[j].getIndex());
						apps.addElement( appNameVersion );
					}
			}
		}
		return apps;
	}
	
	// RUNNING APPLICATIONS
	public static Vector getRunningApps() {
		Vector apps = new Vector();
		ApplicationManager manager = ApplicationManager.getApplicationManager();
		ApplicationDescriptor descriptors[] = manager.getVisibleApplications();
		for ( int i=0; i<descriptors.length; i++ ) {
			String [] appNameVersion = new String[2];
			appNameVersion[Context.APP_NAME] = descriptors[i].getName();
			appNameVersion[Context.APP_VERSION] = descriptors[i].getVersion();
			apps.addElement( appNameVersion );
		}	
		return apps;
	}	
	
	public static Vector getInstalledAppDescriptors( String moduleName ) {
		
		int modHandle = CodeModuleManager.getModuleHandle( moduleName );
		
		if ( modHandle == 0 )
			return null;

		Vector apps = new Vector();
		if ( !CodeModuleManager.isLibrary( modHandle )  ) {
			ApplicationDescriptor descriptors[] = CodeModuleManager.getApplicationDescriptors( modHandle );
			if ( descriptors != null )
				for ( int i=0; i<descriptors.length; i++ ) {
					apps.addElement( descriptors[i] );
				}
		}
		return apps;
		
		/*
		boolean found = false;
		Vector apps = new Vector();
		int [] modHandles = CodeModuleManager.getModuleHandles(false);		
		for ( int i=0; i<modHandles.length && !found; i++ ) {
			String mName = CodeModuleManager.getModuleName(modHandles[i]);
			if ( !CodeModuleManager.isLibrary( modHandles[i] ) && mName.equals(moduleName) ) {
				ApplicationDescriptor descriptors[] = CodeModuleManager.getApplicationDescriptors(modHandles[i]);
				if ( descriptors != null )
					for ( int j=0; j<descriptors.length; j++ ) {
						apps.addElement( descriptors[j] );
					}
				found = true;
			}
		}
		return apps;
		*/
	}

	public static ApplicationDescriptor getRunningAppDescriptor( String moduleName, String [] args ) {		
		ApplicationManager manager = ApplicationManager.getApplicationManager();
		ApplicationDescriptor descriptors[] = manager.getVisibleApplications();
		for ( int i=0; i<descriptors.length; i++ ) {
			// check module name matches
			if ( descriptors[i].getModuleName().equals(moduleName)) {
				if ( args == null )
					return descriptors[i];
							
				// check args match
				String [] dargs = descriptors[i].getArgs();
				boolean found = (dargs.length == args.length);
				if ( found )
					for ( int j=0; j<args.length; j++ ) {
						found = found && dargs[j].equals(args[j]); 
					}
				if ( found )
					return descriptors[i];
			}			
		}	
		return null;		
	}
	
	public static boolean isInstalled( String moduleName ) {
		int handle = CodeModuleManager.getModuleHandle( moduleName );
		return handle != 0;
	}
	
	public static int getPid( String moduleName, String[] args ) {
		int handle = CodeModuleManager.getModuleHandle( moduleName );
		
		// not installed
		if ( handle == 0 )
			return -1;
		
		ApplicationDescriptor [] descriptors = CodeModuleManager.getApplicationDescriptors( handle );

		int i = 0;
		int pid = -1;
		do {
			// check args match
			String [] dargs = descriptors[i].getArgs();
			boolean found = false;
			if ( args == null ) {
				found = true;
			}
			else if ( dargs != null ) {
				found = (dargs.length == args.length);
				if ( found )
					for ( int j=0; j<args.length; j++ ) {
						found = found && dargs[j].equals(args[j]); 
					}
			}
			
			if ( found )
				pid = ApplicationManager.getApplicationManager().getProcessId(descriptors[i]);
			i++;
			
		} while ( i<descriptors.length && pid < 0 ); 
		
		return pid;
	}
	
	public static boolean isRunning( String moduleName, String[] args ) {
		return getPid( moduleName, args ) >= 0;
	}
	
	/**
	 * Blocks until the specified module is running, or a timeout is reached.
	 * @param moduleName Name of module to check.
	 * @param timeoutMillis timeout value. A zero or negative value means no timeout.
	 */
	
	public static boolean blockUntilRunning( String moduleName, long timeoutMillis ) {
		long startTimeMillis = System.currentTimeMillis();
		boolean timedOut = false;		
		boolean running = isRunning( moduleName, null );
		
		while (!running && !timedOut ) {
			try {
				Thread.sleep( 2*1000 );
			}
			catch (InterruptedException ie ) {}
			running = isRunning( moduleName, null );
			timedOut = (timeoutMillis > 0) && (System.currentTimeMillis() - startTimeMillis) > timeoutMillis;
		}
		
		return timedOut;
	}
	
	/**
	 * Blocks until System Application Manager has started.
	 */	
	public static void blockUntilSystemStartup() {
		boolean inStartup = ApplicationManager.getApplicationManager().inStartup();
		while ( inStartup ) {		
			try {
				Thread.sleep( 2*1000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			inStartup = ApplicationManager.getApplicationManager().inStartup();		
		}		
	}
	
	
	/**
	 * Attempts to launch a module, if not running already
	 * @param moduleName module name
	 * @param args arguments to pass to module.  Setting this to null causes it to be ignored.
	 * @param bringToForegroundIfRunning if app running, bring it to the foreground
	 * @return true if the module is running, or if it is installed and this routine attempted to start it. false if not installed.
	 */
	public static boolean relaunch( String moduleName, String[] args, boolean bringToForegroundIfRunning ) {
		// check if running		
		int pid = getPid( moduleName, args );
		if ( pid < 0 ) {
			
		//	Log.d( TAG, "Detected " + moduleName + " not running. Restarting..." );
			
			// check if installed
			int handle = CodeModuleManager.getModuleHandle( moduleName );
			if ( handle != 0 ) {
				ApplicationDescriptor[] ada = CodeModuleManager.getApplicationDescriptors( handle );
				ApplicationDescriptor ad = ada[0];				
				
				try {
					
					if ( args == null )
						ApplicationManager.getApplicationManager().runApplication( ad );
					else {
						ApplicationDescriptor des = new ApplicationDescriptor( ad, args);
						ApplicationManager.getApplicationManager().runApplication( des );
					}
					
				} catch (ApplicationManagerException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
			else {
	//			Log.d( TAG, moduleName + " not installed." );
				return false;
			}
		}
		else {
			if (bringToForegroundIfRunning)
				// if already running, bring to foreground
				ApplicationManager.getApplicationManager().requestForeground( pid );
			return true;
		}
	}
	
	/**
	 * Attempts to launch a module, if not running already
	 * @param moduleName module name
	 * @param args arguments to pass to module.  Setting this to null causes it to be ignored.
	 * @return true if the module is running, or if it is installed and this routine attempted to start it. false if not installed.
	 */
	public static boolean relaunch( String moduleName, String[] args ) {
		return relaunch(moduleName, args, true);
	}
	
	
	/**
	 * Attempts to kill the given module by flooding its event queue.
	 * @param moduleName module name
	 * @param args arguments used to start the module. Setting this to null causes it to be ignored.
	 * @return true if the module was killed. false otherwise.
	 */
	public static boolean attemptKill( String moduleName, String[] args ) {
		ApplicationDescriptor ad = getRunningAppDescriptor(moduleName, args);
		
		// not running?
		if ( ad == null )
			return true;

		int processId = ApplicationManager.getApplicationManager().getProcessId(ad);
				
		long time = System.currentTimeMillis();
		final long MAX_TIME = 2*1000 + time; // 2 seconds
		while ( isRunning(moduleName, args) && time < MAX_TIME ) {
			for ( int i=0; i<100; i++ )
				ApplicationManager.getApplicationManager().postGlobalEvent(processId, net.rim.device.api.lowmemory.LowMemoryManager.GUID_FLASH_LOW, 0, 0, null, null);					
			time = System.currentTimeMillis();
		}
		
		if ( !isRunning(moduleName, args) )
			return true;
		
		return false;
	}
	
	/**
	 * Remove the specified module from the system.
	 * @param moduleName Name of the module.
	 */	
	public static void removeModule( String moduleName ) {
        int handle = CodeModuleManager.getModuleHandle(moduleName);  
        if (handle != 0) {  
     //   	Log.d( TAG, "Attempted to remove module " + moduleName );
        	int success = CodeModuleManager.deleteModuleEx(handle, true);
     //   	Log.d( TAG, "Result: " + success );
        } 		
	}
	
	public static Timer delayedExit( final int status, final long delay ) {
		// Start kill timer. If we don't get a key inject or terminate request before 5 mins, kill application
		Timer timer = new Timer();
		timer.schedule( new TimerTask() {					
			public void run() {
	//			Log.d( TAG, "Forcing exit("+status+")!");
				System.exit(0);
			}
		}, delay );
		return timer;
	}	
}
