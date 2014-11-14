package com.panaceamobile.panacea.sdk.web;

import java.io.InputStream;


public class HttpDownload {

	private static final String TAG = "HttpDownload";
	
	private static final int STREAM_BUFFER_SIZE = 4 * 1024;
	private static final int DEFAULT_READ_TIMEOUT = 20000;
	
	private String mUrl = null;
	private HttpDownload.Observer mObserver = null;
	private int mTimeout = DEFAULT_READ_TIMEOUT;

	/**
	 * HttpDownload.Observer
	 * 
	 * To be implemented by caller to handle download completed/failed
	 * 
	 * @author Mike
	 *
	 */
	public interface Observer {
		public void downloadCompleted(String data);
		public void downloadError(Exception e);
	}
	
	/**
	 * Robustly download a file from a url. 
	 * @param url 
	 * @param filename absolute path to file
	 * @param retries the number of retries
	 * @param observer
	 * @return
	 */
	public static HttpDownload download(String url, HttpDownload.Observer observer) {
		HttpDownload self = new HttpDownload(url, observer);
		self.start();
		return self;
	}

	private void downloadComplete(String data) {
		try {
			mObserver.downloadCompleted(data);
		} catch (Exception e) {
	//		Log.e(TAG, "downloadComplete() observer error: ", e);
		}
	}
	
	private void downloadError(Exception e) {
		try {
			mObserver.downloadError(e);
		} catch (Exception f) {
	//		Log.e(TAG, "downloadError() observer error: ", f);
		}
	}
	
	public HttpDownload(String url, HttpDownload.Observer observer) {
		mUrl = url;
		mObserver = observer;
	}
	
	public void start() {
	//	Log.d(TAG, "start()");

		HttpTransaction t = new HttpTransaction(ContextImpl.getInstance()) {

			protected void transactionError(Exception e) {
	//			Log.e(TAG, "transactionError() " + e.getMessage());
				downloadError(e);
			}

			protected void transactionResponse(InputStream inputStream) throws Exception {
	//			Log.e(TAG, "transactionResponse()");
				
				try {
					StringBuffer sb = new StringBuffer();
		            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
		            int read = 0;
		            while ((read = inputStream.read(buffer)) != -1) {
		            	sb.append(new String(buffer, 0, read, "utf-8"));
		            }
					
					try {
						inputStream.close();	
					} catch (Exception e) {}
	
					downloadComplete(sb.toString());
				} catch (Exception e) {
	//				Log.e(TAG, "transactionResponse() error writing file", e);
					downloadError(e);
				} finally {
					try {
						inputStream.close();	
					} catch (Exception e) {}
				}
			}
		};
		t.setUrl(mUrl);
		t.setReadTimeout(mTimeout);
		t.start();
	}

}
