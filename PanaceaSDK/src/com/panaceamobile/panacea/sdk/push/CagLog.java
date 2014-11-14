package com.panaceamobile.panacea.sdk.push;


/**
 * class CagLog
 * 
 * @author Mike
 * 
 * Platform dependent logging wrapper class
 * 
 */
public class CagLog {

	public static void d(String tag, String msg) {
		Log.d(tag, msg);
	}
	
	public static void i(String tag, String msg, Exception thr) {
		Log.i(tag, msg, thr);
	}

	public static void e(String tag, String msg) {
		Log.e(tag, msg);
	}
	
	public static void e(String tag, String msg, Exception thr) {
		Log.e(tag, msg, thr);
	}

	public static void v(String tag, String msg) {
//		Log.v(tag, msg);
	}
	
	public static void v(String tag, String msg, Exception thr) {
//		Log.v(tag, msg, thr);
	}

}
