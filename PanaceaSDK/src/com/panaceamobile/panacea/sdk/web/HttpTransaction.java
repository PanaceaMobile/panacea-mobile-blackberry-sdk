package com.panaceamobile.panacea.sdk.web;

import java.io.DataOutputStream;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.io.URI;
import net.rim.device.api.io.transport.TransportInfo;

public class HttpTransaction {

	private static final String TAG = "HttpTransaction";

	private final static String CRLF 				= "\r\n";
	private final static String TWO_HYPHENS 		= "--";
	private final static String BOUNDARY 			= "-----------------------------10102754414578508781458777923";
	private final static int BUFFER_SIZE 			= 1024;
	private static final int READ_TIMEOUT 			= 20 * 1000; // 60 secs
	private static final int CONNECT_TIMEOUT 		= 20 * 1000; // 60 secs
	private static final int MAXIMUM_REDIRECTIONS 	= 3;
	private static final int CHUNKLENGTH 			= 1024;

	private String mUrl = null;
	private InputStream mStream = null;
	private volatile Thread mThread = null;
	protected Context mContext = null;
	private String mContentInfo = null;
	private String mUserAgent = null;
	private int mReadTimeout = READ_TIMEOUT;
	private int mConnectTimeout = CONNECT_TIMEOUT;
    private String mPostParams = null;
    private long mStartRange = 0L;
	
	public HttpTransaction(Context context) {
		mContext = context;
		
		// get browser user-agent header
		//WebView v = new WebView(mContext);
		mUserAgent = context.getUserAgentString(); //v.getSettings().getUserAgentString();
	}
	
	public HttpTransaction(Context context, boolean noUserAgent) {
		mContext = context;
	}

	public HttpTransaction setUrl(String url) {
		mUrl = url;
		return this;
	}

	public String getUrl() {
		return mUrl;
	}

	public HttpTransaction setPostData(InputStream stream, String name, String filename, String contentType) {
		mStream = stream;
	
		mContentInfo = "content-disposition: form-data; name=\"" + 
		name +
		"\"; filename=\"" +
		filename + "\"" +
		CRLF +
		"content-type: " +
		contentType +
		CRLF;
				
		return this;
	}
	
	public HttpTransaction setPostData(String contentType, String parameters) {
	
		mContentInfo = contentType;
		mPostParams = parameters;
				
		return this;
	}
	
	public void start() {
	//	UiLog.d(TAG, "start()");
		if (mThread == null) {
			mThread = new Thread(new Runnable() {
	    		public void run() {
	    			startTransaction();
	    		}
	    	}, "HttpTransaction");
			//mThread.setDaemon(true);
			mThread.start();
		}
	}
	
	public void cancel() {
		mThread = null;
		transactionCanceled();
	}

	protected void transactionCanceled() {
//		UiLog.d(TAG, "transactionCanceled() ");
		// to be implemented by derived classes
	}

	protected void transactionCompleted(int httpResponseCode) {
//		UiLog.d(TAG, "transactionCompleted() "+httpResponseCode);
		// to be implemented by derived classes
	}

	protected void transactionError(Exception e) {
//		if (e != null && e.getMessage() != null && !e.getMessage().equalsIgnoreCase("ModelUpToDateException"))
//			UiLog.e(TAG, "transactionError()",e);
		// to be implemented by derived classes
	}

	protected void transactionResponse(InputStream inputStream) throws Exception {
//		UiLog.e(TAG, "transactionResponse()");
		// to be implemented by derived classes
	}
	
	private void startTransaction() {
//		UiLog.d(TAG, "startTransaction() url = " + mUrl);
		
		HttpConnection conn = null;
		try {
			//System.setProperty("http.keepAlive", "false");

			String destination = mUrl;
			int redirectionCount = 0;
			
			while (redirectionCount < MAXIMUM_REDIRECTIONS) {
				URI uri = URI.create(destination);
				String host = uri.getHost();
				
				/*
				boolean redirected = false;
				if (StaticConfig.DEBUG_REDIRECTION_SUPPORTED) {
					// REDIRECTION: any url may be redirected to another using this code. 
					// Uses "assets/cache.properties" for a list of redirected hosts:ports
					Properties properties = new Properties();
					properties.load( mContext.getResourceAsStream( "cache.properties" ) );
					String redirectHost = properties.getProperty(host);
					if (redirectHost != null) {
			        	String newUrl = "http://" + redirectHost + uri.getPath();
			        	UiLog.d(TAG,"redirecting to: "+newUrl);
			        	destination = newUrl;
			        	redirected = true;
					} 
				}
				*/

				// preferred connection order
		    	int[] preferredTransportTypes = {TransportInfo.TRANSPORT_TCP_WIFI, 
												 TransportInfo.TRANSPORT_BIS_B, 
												 TransportInfo.TRANSPORT_MDS,
												 TransportInfo.TRANSPORT_TCP_CELLULAR, 
												 TransportInfo.TRANSPORT_WAP2, 
												 TransportInfo.TRANSPORT_WAP};
				
				// open connection
				conn = mContext.openHttpConnection( destination, Connector.READ_WRITE, true, preferredTransportTypes );			
		        
		        // close the connection after response is sent by server
		        conn.setRequestProperty("Connection", "close");
	
		        /*
				if (StaticConfig.DEBUG_REDIRECTION_SUPPORTED) {
		        	// REDIRECTION: if redirected, then modify the "Host" request header
					if (redirected) {
						conn.setRequestProperty("Host", host);
					}
				}
				*/
				
		        // if configured, do not automatically follow redirects
		        //if (StaticConfig.THROW_ON_HTTP_REDIRECT)
		        //	conn.setInstanceFollowRedirects(false);

	        	// set browser user-agent header
	        	if (mUserAgent != null && mUserAgent.length()>0)
	        		conn.setRequestProperty("User-Agent", mUserAgent);
					
        		// Support resume download 
	        	if (mStartRange > 0L) {
//	        		UiLog.d(TAG, "Range: bytes="+mStartRange+"-");
	        		conn.setRequestProperty("Range", "bytes="+mStartRange+"-");
	        	}
	        	
				// video upload fix
				//conn.setChunkedStreamingMode(CHUNKLENGTH);
		
				// handle post (if there's an associated file)
				//conn.setDoInput(true);
				//conn.setDoOutput(true);
				//conn.setUseCaches(false);
	        	
				byte[] buffer = new byte[BUFFER_SIZE];
				if (mStream != null && mThread != null) {
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
					
					DataOutputStream ds = conn.openDataOutputStream();
					ds.write((TWO_HYPHENS + BOUNDARY + CRLF + mContentInfo + CRLF).getBytes());
					//ds.writeBytes("content-transfer-encoding: binary" + CRLF + CRLF);
						
					int length = -1;
					while((length = mStream.read(buffer)) != -1) {
						ds.write(buffer, 0, length);
					}			
					ds.write(CRLF.getBytes());
					ds.write((TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + CRLF).getBytes());
					mStream.close();
					ds.flush();
					ds.close();
				} else if (mPostParams != null && mThread != null) {
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", mContentInfo);
					conn.setRequestProperty("Content-Length", ""+mPostParams.length());
					
					//Send request
				    DataOutputStream wr = conn.openDataOutputStream();
				    wr.write(mPostParams.getBytes());
				    wr.flush();
				    wr.close();
				} else if (mThread != null) {
					//conn.connect();
				}
					
		        // if configured, throw ProtocolException on redirect
		        if (StaticConfig.THROW_ON_HTTP_REDIRECT) {
			        if (conn.getResponseCode() == HttpConnection.HTTP_MOVED_PERM ||
			        	conn.getResponseCode() == HttpConnection.HTTP_MOVED_TEMP) {
			        	
			        	// get Location 
			        	destination = conn.getHeaderField("Location");
			        	conn.close();
			        	if (destination != null) {
			        		URI redirectedUrl = URI.create(destination);
			        		if (!uri.getHost().equalsIgnoreCase(redirectedUrl.getHost())) {
			        			// not the same host
			        			throw new Exception("Redirect not supported");		
			        		} else {
			        			redirectionCount++;
			        			continue; // redirect to "destination"
			        		}
			        	} else {
			        		throw new Exception("Malformed redirection");
			        	}
			        }
		        } 
		        break;
			}
			
			if (redirectionCount >= MAXIMUM_REDIRECTIONS)
				throw new Exception("too many redirects");
					
			int responseCode = conn.getResponseCode();
//			UiLog.d(TAG, "Response code: " + responseCode + " " + conn.getResponseMessage()); // success = HttpURLConnection.HTTP_OK

			// DEBUG !!!
			// get response headers
			/*
			int pos = 0;
			String header = null;
			while ((header = conn.getHeaderFieldKey(pos)) != null) {
				String value = conn.getHeaderField(pos);
				UiLog.d(TAG, header + ": "+ value);
				pos++;
			}*/
			
			// handle response body
			if (mThread != null) {
				if (responseCode == HttpConnection.HTTP_OK || responseCode == HttpConnection.HTTP_PARTIAL) {
					InputStream is = conn.openInputStream();
					transactionResponse(is);
					is.close();
				} else {
					throw new Exception("Http error: "+responseCode+" "+conn.getResponseMessage());
				}
			}
	
			// clean up
			conn.close();
	
			// return result
			if (mThread != null) {
				transactionCompleted(responseCode);
			}
		} catch (Exception e) {
			//UiLog.e(TAG, "startTransaction", e);
			transactionError(e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @param mReadTimeout the mReadTimeout to set
	 */
	public HttpTransaction setReadTimeout(int mReadTimeout) {
		this.mReadTimeout = mReadTimeout;
		return this;
	}

	/**
	 * @return the mReadTimeout
	 */
	public int getReadTimeout() {
		return mReadTimeout;
	}

	/**
	 * @param mConnectTimeout the mConnectTimeout to set
	 */
	public HttpTransaction setConnectTimeout(int mConnectTimeout) {
		this.mConnectTimeout = mConnectTimeout;
		return this;
	}

	/**
	 * @return the mConnectTimeout
	 */
	public int getConnectTimeout() {
		return mConnectTimeout;
	}

	public long getStartRange() {
		return mStartRange;
	}

	public void setStartRange(long mStartRange) {
		this.mStartRange = mStartRange;
	}
}

