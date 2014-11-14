package com.panaceamobile.panacea.sdk.web;

import java.io.InputStream;


public class HttpPost {

	private static final String TAG = "HttpPost";
	
	private static final int STREAM_BUFFER_SIZE = 4 * 1024;

	private String mUrl = null;
	private HttpPost.Observer mObserver = null;
	private byte[] mBody = null;
	private String mContentType = null;
	private InputStream mBodyInputStream = null;

	/**
	 * HttpDownload.Observer
	 * 
	 * To be implemented by caller to handle download completed/failed
	 * 
	 * @author Mike
	 *
	 */
	public interface Observer {
		public void postCompleted(String data);
		public void postError(Exception e);
	}
	
	/**
	 * Robustly download a file from a url. 
	 * @param url 
	 * @param filename absolute path to file
	 * @param retries the number of retries
	 * @param observer
	 * @return
	 */
	public static HttpPost post(String url, byte[] body, String contentType, HttpPost.Observer observer) {
		HttpPost self = new HttpPost(url, body, contentType, observer);
		self.start();
		return self;
	}

//	public static HttpPost post(String url, InputStream body, String contentType, HttpPost.Observer observer) {
//		HttpPost self = new HttpPost(url, body, contentType, observer);
//		self.start();
//		return self;
//	}
	
	private void postComplete(String data) {
//		try {
			mObserver.postCompleted(data);
//		} catch (Exception e) {
//			System.out.println("Web Request failed: " + e.getMessage());
//		}
	}
	
	private void postError(Exception e) {
		try {
			mObserver.postError(e);
		} catch (Exception f) {
//			Log.e(TAG, "postError() observer error: ", f);
		}
	}
	
	public HttpPost(String url, InputStream body, String contentType, HttpPost.Observer observer) {
		mUrl = url;
		mObserver = observer;
		mBodyInputStream = body;
		mContentType = contentType;
	}
	
	public HttpPost(String url, byte[] body, String contentType, HttpPost.Observer observer) {
		mUrl = url;
		mObserver = observer;
		mBody = body;
		mContentType = contentType;
	}
	
	public void start() {
//		Log.d(TAG, "start()");

		HttpTransaction t = new HttpTransaction(ContextImpl.getInstance()) {

			protected void transactionError(Exception e) {
//				Log.e(TAG, "transactionError() " + e.getMessage());
				postError(e);
			}

			protected void transactionResponse(InputStream inputStream) throws Exception {
//				Log.e(TAG, "transactionResponse()");
				
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
	
					postComplete(sb.toString());
				} catch (Exception e) {
//					Log.e(TAG, "transactionResponse() error writing file", e);
					postError(e);
				} catch (Throwable t)
				{
					System.out.println(t.getMessage());
				}finally {
					try {
						inputStream.close();	
					} catch (Exception e) {}
				}
			}
		};
		t.setUrl(mUrl);
		if (mBody != null)
			t = t.setPostData(mContentType, mBody.toString());
//		else if (mBodyInputStream != null)
//			t = t.setPostData(mBodyInputStream, mContentType);
		t.start();
	}

}
