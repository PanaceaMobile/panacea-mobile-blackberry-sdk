package com.panaceamobile.panacea.sdk.push;

public class Log {
	public static final int LOG_LEVEL_DEBUG = 0;
	public static final int LOG_LEVEL_INFO = 1;
	public static final int LOG_LEVEL_ERROR = 2;
	
	public static int logLevel = LOG_LEVEL_DEBUG;
	
	public static void setLogLevel( int level ) {
		logLevel = level;
	}
	
	public static void d( String tag, String s ) {
		if ( logLevel > LOG_LEVEL_DEBUG )
			return;
		output( "["+tag+"] " + s );
	}

	public static void i( String tag, String s, Exception e) {
		if ( logLevel > LOG_LEVEL_INFO )
			return;
		output( "["+tag+"] " + s );
		e.printStackTrace();
	}

	public static void e( String tag, String s) {
		if ( logLevel > LOG_LEVEL_ERROR )
			return;
		output( "["+tag+"] " + s );
	}
	
	public static void e( String tag, String s, Exception e) {
		if ( logLevel > LOG_LEVEL_ERROR )
			return;
		output( "["+tag+"] " + s + " " + e.getMessage() );
		e.printStackTrace();
	}
	
	protected static void output( String s ) {
		System.out.println( s );		
	}
	
}
