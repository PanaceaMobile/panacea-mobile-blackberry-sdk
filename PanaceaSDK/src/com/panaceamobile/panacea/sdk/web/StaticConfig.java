package com.panaceamobile.panacea.sdk.web;

public class StaticConfig {

	/**
	 * Redirect requests via an alternative host (see /assets/cache.properties)
	 * This allows one to route traffic via a trace program for example which allows
	 * real-time viewing of plain-text xml, http headers etc.
	 */
	public static final boolean DEBUG_REDIRECTION_SUPPORTED 		= true;
	/**
	 * Log HTTP request headers 
	 */
	public static final boolean DEBUG_LOG_HTTP_REQUEST_HEADERS 		= false;
	/**
	 * Log HTTP response headers 
	 */
	public static final boolean DEBUG_LOG_HTTP_RESPONSE_HEADERS 	= false;

	/**
	 * Throw ProtocolException on getting a redirect to a different host
	 */
	public static final boolean THROW_ON_HTTP_REDIRECT				= true;

}

