package com.panaceamobile.panacea.sdk.web;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventInjector;
import net.rim.device.api.ui.Keypad;


public class InputSimUtils {
	
	public static final String TAG = "InputSimUtils"; 
	
	public static final int KEY_TYPE_KEY = 0;
	public static final int KEY_TYPE_NAV = 1;
		
	public static void keyInject( int numNav, int dx, int dy, boolean endWithEnter ) {		
		
		// first thing, check if system is locked and unlock if needed
		if ( ApplicationManager.getApplicationManager().isSystemLocked() ) {
			ApplicationManager.getApplicationManager().unlockSystem();
		}		
		
		for ( int i=0; i<numNav; i++ ){
		//	Log.d( TAG, "inject dx:"+dx + " dy:" + dy );
			EventInjector.invokeEvent( new EventInjector.NavigationEvent( 
					EventInjector.NavigationEvent.NAVIGATION_MOVEMENT, dx, dy, 0)
			);
		}
		
		if ( endWithEnter ) {
	//		Log.d( TAG, "inject enter down");
			EventInjector.invokeEvent( new EventInjector.KeyCodeEvent ( 
					EventInjector.KeyCodeEvent.KEY_DOWN, 
					(char) Keypad.KEY_ENTER, 
					0 )			
			);			
		//	Log.d( TAG, "inject enter up");
			EventInjector.invokeEvent( new EventInjector.KeyCodeEvent ( 
					EventInjector.KeyCodeEvent.KEY_UP, 
					(char) Keypad.KEY_ENTER, 
					0 )
			);
		}
	}

	public static void keyInjectDelayed( int numNav, int dx, int dy, boolean endWithEnter, long millis, Runnable chainedAction ) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {}
		
		keyInject( numNav, dx, dy, endWithEnter );
		
		if (chainedAction != null)
			chainedAction.run();
	}
	
	public static void keyInjectDelayed( int numNav, int dx, int dy, boolean endWithEnter, long millis ) {
		keyInjectDelayed(numNav, dx, dy, endWithEnter, millis, null);
	}
	
	public static int [][] genInputKeys( int numNav, int dx, int dy, boolean endWithEnter ) {
		int [][] inputKeys = new int[1 + (endWithEnter?2:0)][];

		inputKeys[0] = new int[3];
		inputKeys[0][0] = KEY_TYPE_NAV;
		inputKeys[0][1] = numNav;
		inputKeys[0][2] = dx*1000 + dy;
		
		if ( endWithEnter ) {
			inputKeys[1] = new int[4];
			inputKeys[1][0] = KEY_TYPE_KEY;
			inputKeys[1][1] = EventInjector.KeyCodeEvent.KEY_DOWN;
			inputKeys[1][2] = Keypad.KEY_ENTER;
			inputKeys[1][3] = 0;
	
			inputKeys[2] = new int[4];
			inputKeys[2][0] = KEY_TYPE_KEY;
			inputKeys[2][1] = EventInjector.KeyCodeEvent.KEY_UP;
			inputKeys[2][2] = Keypad.KEY_ENTER;
			inputKeys[2][3] = 0;
		}
		
		return inputKeys;
	}
	
	public static void keyInject( int [][] inputKeys ) {
		
		// first thing, check if system is locked and unlock if needed
		if ( ApplicationManager.getApplicationManager().isSystemLocked() ) {
			ApplicationManager.getApplicationManager().unlockSystem();
		}		
		
		for ( int i=0; i<inputKeys.length; i++ ) {
			
			int [] keyEvent = inputKeys[i];
			if ( keyEvent[0] == KEY_TYPE_KEY ) {
				int event = keyEvent[1];
				int name = keyEvent[2];
				int status = keyEvent[3];				
				EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (	event, (char) name,	status ) );			
			}
			else if ( keyEvent[0] == KEY_TYPE_NAV ) {
				int repeat = keyEvent[1];
				int val = keyEvent[2];
				int dx = val / 1000;
				int dy = val % 1000;
				for ( int j=0; j<repeat; j++ ){				
					EventInjector.invokeEvent( new EventInjector.NavigationEvent( 
							EventInjector.NavigationEvent.NAVIGATION_MOVEMENT, dx, dy, 0)
					);
				}
			}
		}		
	}
	
	public static void keyInjectDelayed( int [][] inputKeys, long millis ) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {}
		
		keyInject( inputKeys ); 
	}
	
	public static void keyInject( String script ) {
		
		// parse script and extract nav keys, commands, and normal keys
		// [LEFT|RIGHT|UP|DOWN]		
		// [ENTER|ESCAPE|MENU|DELETE|BACKSPACE|HANGUP]
		// TODO: [ALT|SHIFT]
		// a-zA-Z0-9
		
		if ( script == null )
			return;
		
		if ( ApplicationManager.getApplicationManager().isSystemLocked() ) {
			ApplicationManager.getApplicationManager().unlockSystem();
		}
		
		int i=0;
		while (i<script.length()) {
		
			char c = script.charAt(i);
			if (c=='[' || c==']') {
				// escaped bracket
				if (script.charAt(i+1) == c) {
			//		Log.d(TAG, "escaped " + c );
					EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, c, 0 ) );								
					EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (	EventInjector.KeyCodeEvent.KEY_UP, c, 0 ) );
					i++;
				}
				// floating closing bracket -> return
				else if (c==']') {
					return;
				}
				// start of nav or cmd key
				else if (c=='[') {
					int j=i+1;
					char e = '1';
					do {						
						e = script.charAt(j);
					}
					while (e!=']' && ++j<script.length());						
					
					// if unclosed bracket, return
					if (j>=script.length())
						return;
					
					String cmd = script.substring(i+1, j);
				//	Log.d(TAG,"CMD="+cmd);
					
					if ( cmd.equalsIgnoreCase("left")) {
			//			Log.d(TAG,"LEFT");
						EventInjector.invokeEvent( new EventInjector.NavigationEvent( 
								EventInjector.NavigationEvent.NAVIGATION_MOVEMENT, -1, 0, 0) 
						);						
					}
					else if ( cmd.equalsIgnoreCase("right")) {
			//			Log.d(TAG,"RIGHT");
						EventInjector.invokeEvent( new EventInjector.NavigationEvent( 
								EventInjector.NavigationEvent.NAVIGATION_MOVEMENT, 1, 0, 0) 
						);												
					} 
					else if ( cmd.equalsIgnoreCase("up")) {
			//			Log.d(TAG,"UP");
						EventInjector.invokeEvent( new EventInjector.NavigationEvent( 
								EventInjector.NavigationEvent.NAVIGATION_MOVEMENT, 0, -1, 0) 
						);						
					} 
					else if ( cmd.equalsIgnoreCase("down")) {
			//			Log.d(TAG,"DOWN");
						EventInjector.invokeEvent( new EventInjector.NavigationEvent( 
								EventInjector.NavigationEvent.NAVIGATION_MOVEMENT, 0, 1, 0) 
						);												
					} 
					else if ( cmd.equalsIgnoreCase("enter")) {
			//			Log.d(TAG,"ENTER");
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_ENTER, 0 ) );								
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_UP, (char) Keypad.KEY_ENTER, 0 ) );								
					} 
					else if ( cmd.equalsIgnoreCase("escape")) {
			//			Log.d(TAG,"ESCAPE");
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_ESCAPE, 0 ) );								
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_UP, (char) Keypad.KEY_ESCAPE, 0 ) );								
					} 
					else if ( cmd.equalsIgnoreCase("menu")) {
			//			Log.d(TAG,"MENU");
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_MENU, 0 ) );								
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_UP, (char) Keypad.KEY_MENU, 0 ) );								
					} 
					else if ( cmd.equalsIgnoreCase("delete")) {
			//			Log.d(TAG,"DELETE");
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_DELETE, 0 ) );								
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_UP, (char) Keypad.KEY_DELETE, 0 ) );								
					} 
					else if ( cmd.equalsIgnoreCase("backspace")) {
			//			Log.d(TAG,"BACKSPACE");
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_BACKSPACE, 0 ) );								
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_UP, (char) Keypad.KEY_BACKSPACE, 0 ) );								
					} 
					else if ( cmd.equalsIgnoreCase("hangup")) {
			//			Log.d(TAG,"HANGUP");
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_END, 0 ) );								
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_UP, (char) Keypad.KEY_END, 0 ) );								
					} 
					else if ( cmd.equalsIgnoreCase("lock")) {
			//			Log.d(TAG,"LOCK");
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, (char) Keypad.KEY_LOCK, 0 ) );								
						EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_UP, (char) Keypad.KEY_LOCK, 0 ) );								
					} 
					
					// move to char after closing bracket
					i = j;
				}				
			}
			else {
		//		Log.d(TAG,"std " + c);
				EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (EventInjector.KeyCodeEvent.KEY_DOWN, c, 0 ) );								
				EventInjector.invokeEvent( new EventInjector.KeyCodeEvent (	EventInjector.KeyCodeEvent.KEY_UP, c, 0 ) );				
			}
			
			i++;
		}		
	}	
	
}
