package com.panaceamobile.panacea.sdk.db;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.blackberry.api.messagelist.ApplicationMessageFolder;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.ContentProtectedHashtable;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.io.*;


/**
 * Database Helper to handle all interaction with SQLite DB
 * 
 * @author Cobi Interactive
 */
public class PMDatabaseHelper 
{
//	private static final String TAG = "PMDatabaseHelper";
//
//	/* Database Version */
//	private static final int DATABASE_VERSION = 1;
//	/* Database Name */
//	private static final String DATABASE_NAME = "Panacea"; //Panacea.db
//
//
//	/* Tables name */
//	private static final String TABLE_RECEIVED = "pm_received_messages";
//	private static final String TABLE_SENT = "pm_sent_messages";
//	
//	// All unread messages key
//	public static final String ALL_UNREAD_MESSAGES_SUBJECT = "All updates";
//
//	/* Table Columns names */
//	private static final String KEY_RECEIVED_MESSAGE_ID = "received_message_id";
//	private static final String KEY_SENT_MESSAGE_ID = "sent_message_id";
//	private static final String KEY_SUBJECT = "subject";
//	private static final String KEY_STATUS = "status";
//	private static final String KEY_TEXT = "text";
//	private static final String KEY_CREATED = "created";
//	private static final String KEY_THREAD_ID = "thread_id";
//	private static final String KEY_DEVICE_ID = "device_id";
//	private static final String KEY_DELETED = "deleted";
//	
//	private static final String KEY_APPLICATION_ID = "application_id";
//
//
//	private static final String[] COLUMNS_RECEIVED =
//		{ KEY_SENT_MESSAGE_ID, KEY_RECEIVED_MESSAGE_ID, KEY_SUBJECT, KEY_STATUS, KEY_TEXT,
//			KEY_CREATED, KEY_THREAD_ID, KEY_DEVICE_ID, KEY_APPLICATION_ID };
//
//	private static final String[] COLUMNS_SENT =
//		{ KEY_RECEIVED_MESSAGE_ID, KEY_TEXT, KEY_CREATED, KEY_THREAD_ID, KEY_DEVICE_ID,
//			KEY_APPLICATION_ID };
//
//	//private Context mContext;
//	// com.panacea.sdk.db
//		private static final long DATABASE_KEY = 0xd944d6b9fbb06f7dL;
//		
//		// com.panacea.sdk.db.MESSAGEBOX_KEY
//		public static final long MESSAGEBOX_KEY =0xcb7223aaad10964cL;
//		// com.panacea.sdk.db.DELETEDBOX_KEY
//		public static final long DELETEDBOX_KEY =0xb9e06fa01dbb1f22L;
//
//	/**
//	 * Singleton instance of PMDatabaseHelper
//	 */
//	private static PMDatabaseHelper mInstance = null;
//	
//	
//	private ApplicationMessageFolder _inbox;
//	private ApplicationMessageFolder _outbox;
//	
//	
//	
//
//	/**
//	 * Static access to singleton instance of PMDatabaseHelper
//	 * 
//	 * @param context
//	 * @return PMDatabaseHelper static instance
//	 */
//	public static PMDatabaseHelper getInstance()
//	{
//		if (mInstance == null)
//		{
//			mInstance = new PMDatabaseHelper();
//		}
//		return mInstance;
//	}
//
//	private PMDatabaseHelper()
//	{
//		 
//	}
//	
//	
//	private ContentProtectedHashtable getDatabase()
//	{
//		PersistentObject store = PersistentStore
//				.getPersistentObject(DATABASE_KEY);
//		ContentProtectedHashtable db = (ContentProtectedHashtable) store
//				.getContents();
//
//		if (db == null) {
//			db = new ContentProtectedHashtable();
//			onCreate(db);
//			store.setContents(db);
//			store.commit();
//		}
//		
//		return db;
//		
//	}
//
//	protected void onCreate(ContentProtectedHashtable db)
//	{
//
//		
//		
//		db.put(TABLE_RECEIVED, inbox);
//		db.put(TABLE_SENT, outbox);
//
//	}
//
//
//	public void onUpgrade(ContentProtectedHashtable db, int oldVersion, int newVersion)
//	{
//
//		deleteMessageCache(db);
//	}
//
//
//	public void onDowngrade(ContentProtectedHashtable db, int oldVersion, int newVersion)
//	{
//
//		deleteMessageCache(db);
//	}
//
//
//
//	/**
//	 * Drops all tables
//	 * 
//	 * @param db
//	 */
//	private void deleteMessageCache(ContentProtectedHashtable db)
//	{
//		
//
//		db.remove(TABLE_RECEIVED);
//		db.remove(TABLE_RECEIVED);
//
//		onCreate(db);
//	}
//
//	/**
//	 * Drops all tables in Database
//	 */
//	public void deleteMessageCache()
//	{
//		ContentProtectedHashtable db = this.getDatabase();
//		deleteMessageCache(db);
//		
//	}
//	
//	private void saveDatabase(ContentProtectedHashtable db)
//	{
//		PersistentObject store = PersistentStore
//			.getPersistentObject(DATABASE_KEY);
//		
//		
//		store.setContents(db);
//		
//		
//		store.commit();
//	}
//
//	/**
//	 * Adds a row to table for a {@link PMReceivedMessage}
//	 * 
//	 * @param message
//	 */
//	public synchronized void addReceivedMessage(PMReceivedMessage message)
//	{
//		
//		_inbox.fireElementAdded(message);
//	}
//
//	/**
//	 * Adds a row to table for a {@link PMSentMessage}
//	 * 
//	 * @param message
//	 */
//	public synchronized void addSentMessage(PMSentMessage message)
//	{
//		// 1. get reference to writable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 3. try updating row
//		_inbox.ad(message); //selection args
//		
//		
//		// 4. save
//		saveDatabase(db);
//	}
//
//	/**
//	 * Marks a single message as deleted.
//	 * 
//	 * @param message
//	 * @param deleted
//	 *        if true marks as deleted otherwise not deleted
//	 */
//	public synchronized void markMessage(PMMessage message, boolean deleted)
//	{
//
//		// 1. get reference to writable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. create ContentValues to add key "column"/value
//		String tableName = (message instanceof PMReceivedMessage) ? TABLE_RECEIVED : TABLE_SENT;
//		ReadableListImpl table = (ReadableListImpl)db.get(tableName);
//		
//		
//		message.setDeleted(deleted?1:0);
//		
//		table.addMessage(message);
//
//		db.put(tableName, table);
//		saveDatabase(db);
//	}
//
//	/**
//	 * Marks all the messages in a thread as deleted
//	 * 
//	 * @param threadID
//	 * @param if true marks as deleted otherwise not deleted
//	 */
//	public synchronized void markThread(int threadID, boolean deleted)
//	{	
//		
//		// 1. get reference to writable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. create ContentValues to add key "column"/value
//		ReadableListImpl sent = (ReadableListImpl)db.get(TABLE_SENT);
//		ReadableListImpl received = (ReadableListImpl)db.get(TABLE_RECEIVED); 
//
//		Vector threadMessages = getMessagesForThreadId(threadID);
//		
//		for(int i = 0;i<threadMessages.size();i++)
//		{
//			PMMessage message = (PMMessage)threadMessages.elementAt(i);
//			
//			message.setDeleted(deleted?1:0);
//			if(message instanceof PMReceivedMessage)
//			{
//				received.put(new Integer(message.getReceivedMessageId()), message);
//			}
//			else
//			{
//				sent.put(new Integer(((PMSentMessage)message).getSentMessageId()),message);
//			}
//		}
//		db.put(TABLE_SENT,sent);
//		db.put(TABLE_RECEIVED,received);
//		saveDatabase(db);
//	}
//	
//	/**
//	 * Marks an entire subject as deleted
//	 * 
//	 * @param subject
//	 * @param deleted
//	 *        if true marks as deleted otherwise not deleted
//	 */
//	public synchronized void markSubject(String subject, boolean deleted)
//	{
//		// 1. get reference to writable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. create ContentValues to add key "column"/value
//		ReadableListImpl sent = (ReadableListImpl)db.get(TABLE_SENT);
//		ReadableListImpl received = (ReadableListImpl)db.get(TABLE_SENT); 
//
//		Vector threadMessages = getMessagesForSubject(subject);
//		
//		for(int i = 0;i<threadMessages.size();i++)
//		{
//			PMMessage message = (PMMessage)threadMessages.elementAt(i);
//			
//			message.setDeleted(deleted?1:0);
//			if(message instanceof PMReceivedMessage)
//			{
//				received.put(new Integer(message.getReceivedMessageId()), message);
//			}
//			else
//			{
//				sent.put(new Integer(((PMSentMessage)message).getSentMessageId()), message);
//			}
//		}
//		db.put(TABLE_SENT,sent);
//		db.put(TABLE_RECEIVED,received);
//		saveDatabase(db);
//	}
//	
//	/**
//	 * removes the deleted flag from all messages in the database, thereby
//	 * 'undeleting' them
//	 */
//	public void unmarkAll()
//	{
//		// 1. get reference to writable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. create ContentValues to add key "column"/value
//		ReadableListImpl sentTable = (ReadableListImpl)db.get(TABLE_SENT);
//		ReadableListImpl receivedTable = (ReadableListImpl)db.get(TABLE_RECEIVED);
//
//		// 3. try updating row
//		Enumeration sentKeys = sentTable.keys();
//		while(sentKeys.hasMoreElements())
//		{
//			PMSentMessage message = (PMSentMessage) sentTable.get(sentKeys.nextElement());
//			message.setDeleted(0);
//			sentTable.put(new Integer(message.getSentMessageId()), message);
//		}
//
//		Enumeration receivedKeys = sentTable.keys();
//		while(receivedKeys.hasMoreElements())
//		{
//			PMSentMessage message = (PMSentMessage) receivedTable.get(receivedKeys.nextElement());
//			message.setDeleted(0);
//			receivedTable.put(new Integer(message.getSentMessageId()), message);
//		}
//		db.put(TABLE_SENT, sentTable);
//		db.put(TABLE_RECEIVED, receivedTable);
//		saveDatabase(db);
//	}
//
//	/**
//	 * updates the status of a received message message. AKA marks it as read.
//	 * 
//	 * @param receivedMessageId
//	 */
//	public synchronized void markMessageAsRead(int receivedMessageId)
//	{
//		// 1. get reference to writable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. create ContentValues to add key "column"/value
//		ReadableListImpl receivedTable = (ReadableListImpl)db.get(TABLE_RECEIVED);
//		
//		PMReceivedMessage message = (PMReceivedMessage)receivedTable.get(new Integer(receivedMessageId));
//		message.setStatus(128);
//		receivedTable.put(new Integer(receivedMessageId), message);
//		db.put(TABLE_RECEIVED, receivedTable);
//		saveDatabase(db);
//		
//}
//	
//	/**
//	 * Adds a list of messages to relevant db tables
//	 * 
//	 * @param messages
//	 */
//	public void addMessages(Vector messages)
//	{
//		for (int i = 0; i<messages.size();i++)
//		{
//			if (messages.elementAt(i) instanceof PMReceivedMessage)
//				addReceivedMessage((PMReceivedMessage) messages.elementAt(i));
//			else
//				addSentMessage((PMSentMessage) messages.elementAt(i));
//		}
//	}
//
//	/**
//	 * Retrieves a received message from database for given message id
//	 * 
//	 * @param messageID
//	 * @return PMMessage
//	 */
//	public synchronized PMMessage getReceivedMessage(int messageID)
//	{
//		// 1. get reference to readable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. get table
//		ReadableListImpl receivedTable = (ReadableListImpl)db.get(TABLE_RECEIVED);
//		// 4. build object
//		PMMessage message = (PMMessage)receivedTable.get(new Integer(messageID));
//		
//		return message;
//	}
//
//	/**
//	 * Retrieves a sent message from database for given message id
//	 * 
//	 * @param messageID
//	 * @return PMMessage
//	 */
//	public synchronized PMMessage getSentMessage(int messageID)
//	{
//		// 1. get reference to readable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. get table
//		ReadableListImpl receivedTable = (ReadableListImpl)db.get(TABLE_SENT);
//		// 4. build object
//		PMMessage message = (PMMessage)receivedTable.get(new Integer(messageID));
//		
//		return message;
//	
//	}
//
////	private PMReceivedMessage receivedMessageFromCursor(Cursor cursor)
////	{
////		PMReceivedMessage message = new PMReceivedMessage();
////		message.setReceivedMessageId(Integer.parseInt(cursor.(0)));
////		message.setSubject(cursor.getString(1));
////		message.setStatus(Integer.parseInt(cursor.getString(2)));
////		message.setText(cursor.getString(3));
////		message.setCreated(cursor.getString(4));
////		message.setThreadID(Integer.parseInt(cursor.getString(5)));
////		message.setDeviceID(Integer.parseInt(cursor.getString(6)));
////		message.setApplicationID(Integer.parseInt(cursor.getString(7)));
////
////		return message;
////	}
////
////	private PMSentMessage sentMessageFromCursor(Cursor cursor)
////	{
////		PMSentMessage message = new PMSentMessage();
////		message.setSentMessageId(Integer.parseInt(cursor.getString(0)));
////		message.setReceivedMessageId(Integer.parseInt(cursor.getString(1)));
////		message.setText(cursor.getString(2));
////		message.setCreated(cursor.getString(3));
////		message.setThreadID(Integer.parseInt(cursor.getString(4)));
////		message.setDeviceID(Integer.parseInt(cursor.getString(5)));
////		message.setApplicationID(Integer.parseInt(cursor.getString(6)));
////
////		return message;
////	}
//
//	/**
//	 * List of all received messages
//	 * 
//	 * @return List of all received messages
//	 */
//	public synchronized ReadableList getStoredMessages()
//	{
//		// 1. get reference to readable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. get table
//		ApplicationMessageFolder receivedTable = (ApplicationMessageFolder)db.get(TABLE_RECEIVED);
//		
//		// 3. return all messages
//		return receivedTable.getMessages();
//	}
//
//	/**
//	 * List of all received messages
//	 * 
//	 * @return List of all received messages
//	 */
//	public synchronized ReadableList getAllSentMessages()
//	{
//		// 1. get reference to readable DB
//		ContentProtectedHashtable db = this.getDatabase();
//
//		// 2. get table
//		ApplicationMessageFolder receivedTable = (ApplicationMessageFolder)db.get(TABLE_SENT);
//		
//		// 3. return all messages
//		return receivedTable.getMessages();
//	}
//	/**
//	 * returns the largest (and newest) received message id
//	 * 
//	 * @return latest message id
//	 */
//	public Integer getLastReceivedMessageId()
//	{
//		ContentProtectedHashtable db = this.getDatabase();
//		
//		Enumeration keys = 	((ReadableListImpl)db.get(TABLE_RECEIVED)).keys();
//		Integer lastmessage = new Integer(-1);
//		while(keys.hasMoreElements())
//		{
//			Integer newKey = (Integer) keys.nextElement();
//			if (newKey.intValue() >  lastmessage.intValue());
//				lastmessage=newKey;
//		}
//
//		return lastmessage;
//	}
//
//	public void setApplicationFolders(ApplicationMessageFolder inbox, ApplicationMessageFolder outbox)
//	{
//		_inbox = inbox;
//		_outbox = outbox;
//	}
//	
//	
//	/**
//	 * returns the largest (and newest) sent message id
//	 * 
//	 * @return latest message id
//	 */
//	public Integer getLastSentMessageId()
//	{
//		ContentProtectedHashtable db = this.getDatabase();
//		
//		Enumeration keys = 	((ReadableListImpl)db.get(TABLE_SENT)).keys();
//		Integer lastmessage = new Integer(-1);
//		while(keys.hasMoreElements())
//		{
//			Integer newKey = (Integer) keys.nextElement();
//			if (newKey.intValue() >  lastmessage.intValue());
//				lastmessage=newKey;
//		}
//
//		return lastmessage;
//	}
//
//	/**
//	 * Returns a list of unique subjects and their respective unread count. This
//	 * includes "All updates"
//	 * 
//	 * @return HashMap of unique subjects and how many unread messages each
//	 *         subject has
//	 */
//	public Hashtable getSubjectCounts()
//	{
//	
//		ContentProtectedHashtable db = this.getDatabase();
//
//				
//		ReadableListImpl received = (ReadableListImpl)db.get(TABLE_RECEIVED);
//		Enumeration receivedKeys = received.keys();
//		Hashtable unreadSubjectCounts = new Hashtable();
//		int totalCount=0;
//		while(receivedKeys.hasMoreElements())
//		{
//			PMReceivedMessage message = (PMReceivedMessage)received.get(receivedKeys.nextElement());
//			if(message.getStatus()!=128)
//			{
//				totalCount++;
//				Integer currentCount = (Integer)unreadSubjectCounts.get(message.getSubject());
//				if(currentCount==null)
//					currentCount = new Integer(0);
//				unreadSubjectCounts.put(message.getSubject(), new Integer(currentCount.intValue()+1));
//			}
//		}
//		
//		unreadSubjectCounts.put(ALL_UNREAD_MESSAGES_SUBJECT, new Integer(totalCount));
//
//		return unreadSubjectCounts;
//	}
//
//	
//
//	/**
//	 * Returns a sorted list of received and sent messages for the thread id.
//	 * Sorted oldest to newest
//	 * 
//	 * @param threadId
//	 * @return List of messages in given thread
//	 */
//	public Vector getMessagesForThreadId(int threadId)
//	{
//		ContentProtectedHashtable db = this.getDatabase();
//
//		
//		Hashtable queryResults = new Hashtable();
//		SimpleSortingVector datesSorted = new SimpleSortingVector();
//		datesSorted.setSortComparator(new Comparator() {
//
//	        public int compare(Object o1, Object o2) {
//
//	            long o1L = ((Long)o1).longValue();
//	            long o2L = ((Long)o2).longValue();
//
//	            if(o1L<o2L)
//	            	return -1;
//	            else if(o1L == o2L)
//	            	return 0;
//	            else 
//	            	return 1;
//	            
//	        }
//
//	        public boolean equals(Object obj) {
//	            return compare(this, obj) == 0;
//	          }
//	    });
//
//		Vector results = new Vector();
//		
//		ReadableListImpl received = (ReadableListImpl)db.get(TABLE_RECEIVED);
//		ReadableListImpl sent = (ReadableListImpl)db.get(TABLE_SENT);
//		Enumeration receivedKeys = received.keys();
//		
//		while(receivedKeys.hasMoreElements())
//		{
//			PMReceivedMessage message = (PMReceivedMessage)received.get((Integer)receivedKeys.nextElement());
//			
//			if( message.getThreadID() == threadId)
//			{				
//				Long time = new Long(message.getCreated().getTime());
//				queryResults.put(time,message);
//				datesSorted.addElement(time);
//			}
//					
//		}
//		datesSorted.reSort();
//		
//		for(int i =0;i<datesSorted.size();i++)
//		{
//			results.addElement(queryResults.get(datesSorted.elementAt(i)));
//		}
//		return results;
//		
//	}
//
//
//	/**
//	 * Returns all received messages that have not been read yet. This is used
//	 * when displaying notifications.
//	 * 
//	 * @return list of unread received messages
//	 */
//	public Vector getAllUnreadMessages()
//	{
//		ContentProtectedHashtable db = this.getDatabase();
//		ReadableListImpl received = (ReadableListImpl)db.get(TABLE_RECEIVED);
//		Enumeration receivedKeys = received.keys();
//		Vector results = new Vector();
//		
//		while(receivedKeys.hasMoreElements())
//		{
//			PMReceivedMessage message = (PMReceivedMessage)received.get(receivedKeys.nextElement());
//			if(message.getStatus()!=128)
//			{
//				results.addElement(message);
//			}
//		}
//		
//		
//
//		return results;
//	}
  
}
