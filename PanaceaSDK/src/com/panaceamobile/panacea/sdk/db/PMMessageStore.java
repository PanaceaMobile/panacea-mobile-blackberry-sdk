package com.panaceamobile.panacea.sdk.db;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.push.LinkedHashtable;
import com.panaceamobile.panacea.service.PMMenuItems;

import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.messagelist.ApplicationIcon;
import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolder;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderListener;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderRegistry;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.ContentProtectedHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.SimpleSortingVector;


public class PMMessageStore implements ApplicationMessageFolderListener {

    private ReadableListImpl _inboxMessages;
    private ReadableListImpl _deletedMessages;
    private ApplicationMessageFolder _mainFolder;
    private ApplicationMessageFolder _deletedFolder;
    
    // com.panacea.sdk.db
 	private static final long DATABASE_KEY = 0xd944d6b9fbb06f7dL;
 	//private static final long DATABASE_KEY = long net.rim.device.api.util.StringUtilities.stringHashToLong(Application.getApplication().getClass().getName());

	// com.panacea.sdk.db.MESSAGEBOX_KEY
	public static final long MESSAGEBOX_KEY =0xcb7223aaad10964cL;
	// com.panacea.sdk.db.DELETEDBOX_KEY
	public static final long DELETEDBOX_KEY =0xb9e06fa01dbb1f22L;

	private static PMMessageStore instance;
	// All unread messages key
	public static final String ALL_UNREAD_MESSAGES_SUBJECT = "All updates";
    /**
     * Creates a new MessageListDemoStore object
     */
    private PMMessageStore()
    {
        _inboxMessages = new ReadableListImpl();
        _deletedMessages = new ReadableListImpl();
        
       
    }
    
    
    
    
    /**
     * Gets the singleton instance of the MessageListDemoStore
     * 
     * @return The singleton instance of the MessagelistDemoStore
     */
    public static PMMessageStore getInstance()
    {

		if (instance == null)
		{
		
			instance = new PMMessageStore();

			
			instance.initializeDatabase();
			
			
				
			if(instance._mainFolder==null && instance._deletedFolder==null)
			{
				ApplicationMessageFolderRegistry reg = ApplicationMessageFolderRegistry.getInstance();
		        if(reg.getApplicationFolder(PMMessageStore.MESSAGEBOX_KEY) == null)
		        {
		        	instance.getMessagesFromPersistedStorage();
		        	
		        	instance.initMessageOperations();
		        	
		        }
		        else
		        {
		        	
		        	instance.fetchApplicationMessageFolders();
		        	if(instance._inboxMessages==null && instance._deletedMessages ==null)
		        	{
		        		instance.getMessagesFromPersistedStorage();
		        	}
		        }
			}
			
			ApplicationIndicator ic = ApplicationIndicatorRegistry.getInstance().getApplicationIndicator();
			if(ic==null)
			{
				EncodedImage indicatorIcon = EncodedImage.getEncodedImageResource("img/icon_notify_small.png");
				ApplicationIcon applicationIcon = new ApplicationIcon(indicatorIcon);
				ApplicationIndicatorRegistry.getInstance().register(applicationIcon, false, false);
				updateIndicator(instance.getUnreadMessageCount());
			}

	        
		}
		
		
		
		
		
		return instance;
    }
    private int getUnreadMessageCount()
    {
    	int count=0;
    	for(int i = 0; i<_inboxMessages.size();i++)
		{
			PMMessage message = (PMMessage)_inboxMessages.getAt(i);
			if(message instanceof PMReceivedMessage && ((PMReceivedMessage) message).isUnread())
			{
				count ++;
			}
		}
    	
    	return count;
    }
    private void initializeDatabase()
    {
    	//Initialize and save the Persistent store (so that the messages are persisted)
		PersistentObject store = PersistentStore
			.getPersistentObject(DATABASE_KEY);
		ContentProtectedHashtable db = (ContentProtectedHashtable) store
			.getContents();
		
		
		// Check if this application registered folders already
      
        
		if(db==null)
		{    
			db = new ContentProtectedHashtable();	   		
			store.setContents(db);
	   		store.commit();
		}
		
    }
    private void getMessagesFromPersistedStorage()
    {
    	PersistentObject store = PersistentStore
    			.getPersistentObject(DATABASE_KEY);
    		ContentProtectedHashtable db = (ContentProtectedHashtable) store
    			.getContents();
    	synchronized(db)
        {
			
            ReadableListImpl inbox = (ReadableListImpl) db.get(new Long(MESSAGEBOX_KEY));
            ReadableListImpl deleted = (ReadableListImpl) db.get(new Long(DELETEDBOX_KEY));
               
               if(inbox!=null || deleted!=null)
               {
            	   _inboxMessages = inbox;
            	   _deletedMessages = deleted;
            	   
               }
               	
        }
    }
    private void fetchApplicationMessageFolders()
    {
    	ApplicationMessageFolderRegistry reg = ApplicationMessageFolderRegistry.getInstance();
    	_mainFolder = reg.getApplicationFolder(PMMessageStore.MESSAGEBOX_KEY);
    	_deletedFolder = reg.getApplicationFolder(PMMessageStore.DELETEDBOX_KEY);
    	if(_mainFolder.getMessages().size()>0 || _deletedFolder.getMessages().size()>0)
    	{
    		_inboxMessages = (ReadableListImpl) _mainFolder.getMessages();
    		_deletedMessages = (ReadableListImpl) _deletedFolder.getMessages();
    	}
    	
    }
    public static void updateIndicator(int amountOfChanges)
    {
    	 ApplicationIndicator _indicator = ApplicationIndicatorRegistry.getInstance().getApplicationIndicator();
	   if(_indicator == null||amountOfChanges==0)
       {
           return;
       }    
        _indicator.setValue(_indicator.getValue() +amountOfChanges);
        if(_indicator.getValue() <= 0)
        {
            _indicator.setVisible(false);
            HomeScreen.updateIcon(Bitmap.getBitmapResource("img/icon.png"));
        }
        else
        {
        	_indicator.setVisible(true);
            HomeScreen.updateIcon(Bitmap.getBitmapResource("img/icon_notify.png"));
        } 
	}
   
    public static void clearIndicator()
    {
    	 ApplicationIndicator _indicator = ApplicationIndicatorRegistry.getInstance().getApplicationIndicator();
	   if(_indicator == null)
       {
           return;
       }    
        _indicator.setValue(0);
        _indicator.setVisible(false);
       
	}
   
    private void initMessageOperations()
	{
		// 1. Register folders and application descriptors ----------------------

        ApplicationMessageFolderRegistry reg = ApplicationMessageFolderRegistry.getInstance();


        ApplicationDescriptor mainDescr = ApplicationDescriptor.currentApplicationDescriptor();
        
        String test = mainDescr.getName();
        

      

       ApplicationMessageFolder inbox = reg.registerFolder(PMMessageStore.MESSAGEBOX_KEY, "MessageBox", getInboxMessages(), false);
        
        
        ApplicationMessageFolder outbox = reg.registerFolder(PMMessageStore.DELETEDBOX_KEY, "DeletedBox", getDeletedMessages(), false);

        // Register as a listener for callback notifications
        inbox.addListener(this, ApplicationMessageFolderListener.MESSAGE_DELETED | ApplicationMessageFolderListener.MESSAGE_MARKED_OPENED
            | ApplicationMessageFolderListener.MESSAGE_MARKED_UNOPENED , mainDescr);
       // outbox.addListener(this, ApplicationMessageFolderListener.MESSAGE_DELETED, mainDescr);

        this.setApplicationFolders(inbox, outbox);

        // We've registered two folders, specify root folder name for the
        // [View Folder] screen.
        reg.setRootFolderName( mainDescr.getName());

        // 3. Register message menu items --------------------------------------

        ApplicationMenuItem openMenuItem = new PMMenuItems.OpenContextMenu(0x230010);

        ApplicationMenuItem[] openedGuiMenuItems = new ApplicationMenuItem[] {openMenuItem};
       
//        reg.registerMessageMenuItems(PMMessage.MESSAGE_TYPE, ApplicationMessage.Status.UNOPENED, newGuiMenuItems, uiCallbackDescr);
//        reg.registerMessageMenuItems(PMMessage.MESSAGE_TYPE, ApplicationMessage.Status.UNOPENED, newDaemonMenuItems, serviceDescr);
        reg.registerMessageMenuItems(PMMessage.MESSAGE_TYPE, ApplicationMessage.Status.OPENED, openedGuiMenuItems, mainDescr);
//        reg.registerMessageMenuItems(PMMessage.MESSAGE_TYPE, ApplicationMessage.Status.OPENED, openedDaemonMenuItems, serviceDescr);
        //reg.registerMessageMenuItems(PMMessage.MESSAGE_TYPE, PMReceivedMessage.Status, deletedGuiMenuItems, uiCallbackDescr);

//        reg.setBulkMarkOperationsSupport(PMMessage.MESSAGE_TYPE, ApplicationMessage.Status.UNOPENED, true, false);
//        reg.setBulkMarkOperationsSupport(PMMessage.MESSAGE_TYPE, ApplicationMessage.Status.OPENED, false, true);
	}


     /**
     * Moves a message into the deleted folder
     * 
     * @param message The message to move to the deleted folder
     */
    public void markMessageDeleted(PMMessage message, boolean delete)
    {
    	if(message instanceof PMReceivedMessage && ((PMReceivedMessage)message).isUnread())
	    {
	           updateIndicator(delete?-1:1);
	    }
	        
	    message.setDeleted(delete?1:0);
	    
    	if(delete)
    	{	       
	        _inboxMessages.removeMessage(message);
	        _deletedMessages.addMessage(message);
	        _deletedFolder.fireElementAdded(message); 
    	}
    	else
    	{    		
 	        _inboxMessages.addMessage(message);
 	        _deletedMessages.removeMessage(message);
 	         _mainFolder.fireElementAdded(message);
    	}  
    	persistStore();
    }

    private void persistStore()
    {
    	PersistentObject store = PersistentStore
				.getPersistentObject(DATABASE_KEY);
		ContentProtectedHashtable db = (ContentProtectedHashtable) store
				.getContents();


        synchronized(db)
        {
            db.put(new Long(MESSAGEBOX_KEY), _inboxMessages);
            db.put(new Long(DELETEDBOX_KEY), _deletedMessages);
            store.setContents(db);
            store.commit();
        }

    }
    
    


    public void markMessageRead(PMReceivedMessage message)
    {
    	message.setStatus(128);
    	_mainFolder.fireElementUpdated(message, message);
    	updateIndicator(-1);
    	persistStore();
    }
    
    public void markMessageAsNew(PMReceivedMessage message)
    {
    	message.setStatus(0);
    	_mainFolder.fireElementUpdated(message, message);
    	updateIndicator(1);
    	persistStore();
    }

    /**
     * Adds a message to the inbox
     * 
     * @param message The message to add to the inbox
     */
    public void addInboxMessage(PMMessage message)
    {
        _inboxMessages.addMessage(message);

        if(message instanceof PMReceivedMessage && ((PMReceivedMessage)message).isUnread())
        {
           updateIndicator(1); 
           _mainFolder.fireElementAdded(message);
        }
       
        persistStore();
        
        
    }
    
    
    
    
    /**
	 * Returns a sorted list of received and sent messages for the thread id.
	 * Sorted oldest to newest
	 * 
	 * @param threadId
	 * @return List of messages in given thread
	 */
	public Vector getMessagesForThreadId(int threadId)
	{
		Hashtable queryResults = new Hashtable();
		SimpleSortingVector datesSorted = new SimpleSortingVector();
		datesSorted.setSortComparator(new Comparator() {

	        public int compare(Object o1, Object o2) {

	            long o1L = ((Long)o1).longValue();
	            long o2L = ((Long)o2).longValue();

	            if(o1L<o2L)
	            	return -1;
	            else if(o1L == o2L)
	            	return 0;
	            else 
	            	return 1;
	            
	        }

	        public boolean equals(Object obj) {
	            return compare(this, obj) == 0;
	          }
	    });

		
		for(int i = 0;i<_inboxMessages.size();i++)
		{
			PMMessage message = (PMMessage)_inboxMessages.getAt(i);
			if( message.getThreadID() == threadId)
			{				
				Long time = new Long(message.getCreated().getTime());
				queryResults.put(time,message);
				datesSorted.addElement(time);
			}
		}
		
		datesSorted.reSort();
		Vector results = new Vector();
		for(int i=0;i<datesSorted.size();i++)
		{
			results.addElement(queryResults.get(datesSorted.elementAt(i)));
		}
		return results;
	}
	
	
	/**
	 * Returns the latest received message with a given subject for each
	 * different thread id. "All updates" will return the latest received
	 * message for each subject with a different thread id.
	 * 
	 * @param subject
	 * @return List of latest receivedMessage for each theadID
	 */
	public Vector getMessagesForSubject(String subject)
	{
		Hashtable data = new Hashtable();
		
		
		for(int i = 0; i<_inboxMessages.size();i++)
		{
			PMMessage message = (PMMessage)_inboxMessages.getAt(i);
			if(message instanceof PMReceivedMessage && (subject==null || message.getSubject().equals(subject)||subject.equals(ALL_UNREAD_MESSAGES_SUBJECT)))
			{
				if(data.get(new Integer(message.getThreadID()))!=null)
				{
					Date created = ((PMReceivedMessage)data.get(new Integer(message.getThreadID()))).getCreated();
					if(created.getTime() < message.getCreated().getTime())
						data.put(new Integer(message.getThreadID()),message);
				}
				else
					data.put(new Integer(message.getThreadID()),message);
			}
		}
				
		Vector result = new Vector();
		Enumeration elements = data.elements();
		while(elements.hasMoreElements())
			result.addElement(elements.nextElement());
		
		return result;
		
	}

	public void markMultipleAsDeleted(Vector messages,boolean deleted)
	{
		int newCount=0;
		for(int i = 0;i<messages.size();i++)
		{
			PMMessage message = (PMMessage)messages.elementAt(i);
			
			
			if(deleted)
			{
				if(message instanceof PMReceivedMessage && ((PMReceivedMessage)message).isUnread())
			    {
			           newCount += -1;
			    }
				message.setDeleted(1);       
		        _inboxMessages.removeMessage(message);
		        _deletedMessages.addMessage(message);
			}
			else
			{
				if(message instanceof PMReceivedMessage && ((PMReceivedMessage)message).isUnread())
			    {
			           newCount += 1;
			    }
				message.setDeleted(0);       
				_deletedMessages.removeMessage(message);
				_inboxMessages.addMessage(message);	
			}
		}
		
		updateIndicator(newCount);
		if(deleted)
			_deletedFolder.fireReset();
		else
			_mainFolder.fireReset();
		persistStore();
	}
	
	public void markMultipleAsRead(Vector messages,boolean read)
	{
		int newCount=0;
		for(int i = 0;i<messages.size();i++)
		{
			PMMessage message = (PMMessage)messages.elementAt(i);
			
			
			if(read)
			{
				if(message instanceof PMReceivedMessage && ((PMReceivedMessage)message).isUnread())
			    {
			           newCount += -1;
			           ((PMReceivedMessage)message).setStatus(128);
			    }
				
			}
			else
			{
				if(message instanceof PMReceivedMessage && !((PMReceivedMessage)message).isUnread())
			    {
			           newCount += -1;
			           ((PMReceivedMessage)message).setStatus(0);
			    }
				
			}
		}
		
		updateIndicator(newCount);
		_mainFolder.fireReset();
		persistStore();
	}
	
	
	
	/**
	 * Marks an entire subject as deleted
	 * 
	 * @param subject
	 * @param deleted
	 *        if true marks as deleted otherwise not deleted
	 */
	public synchronized void markSubject(String subject, boolean deleted)
	{
		
		Vector subjectMessages = getMessagesForSubject(subject);
		markMultipleAsDeleted(subjectMessages, deleted);
		
	}
	
	
	/**
	 * Marks all the messages in a thread as deleted
	 * 
	 * @param threadID
	 * @param if true marks as deleted otherwise not deleted
	 */
	public synchronized void markThread(int threadID, boolean deleted)
	{	
		Vector threadMessages = getMessagesForThreadId(threadID);
		markMultipleAsDeleted(threadMessages, deleted);		
	}
	
	
	/**
	 * returns the largest (and newest) sent message id
	 * 
	 * @return latest message id
	 */
	public Integer getLastSentMessageId()
	{
		
		Enumeration keys = _inboxMessages.keys();
		Integer lastmessage = new Integer(-1);
		while(keys.hasMoreElements())
		{
			Integer newKey = (Integer) keys.nextElement();
			if(_inboxMessages.get(newKey) instanceof PMSentMessage)
			{
				if (newKey.intValue() >  lastmessage.intValue())
					lastmessage=newKey;
			}
		}

		keys = _deletedMessages.keys();
		while(keys.hasMoreElements())
		{
			Integer newKey = (Integer) keys.nextElement();
			if(_deletedMessages.get(newKey) instanceof PMSentMessage)
			{
				if (newKey.intValue() >  lastmessage.intValue())
					lastmessage=newKey;
			}
		}

		
		return lastmessage;
	}
	
	/**
	 * Returns a list of unique subjects and their respective unread count. This
	 * includes "All updates"
	 * 
	 * @return HashMap of unique subjects and how many unread messages each
	 *         subject has
	 */
	public LinkedHashtable getSubjectCounts()
	{
	
		SimpleSortingVector sortedMessages = new SimpleSortingVector();
		sortedMessages.setSortComparator(new Comparator() {

	        public int compare(Object o1, Object o2) {

	            long o1L = ((PMMessage)o1).getCreated().getTime();
	            long o2L = ((PMMessage)o2).getCreated().getTime();

	            if(o1L>o2L)
	            	return -1;
	            else if(o1L == o2L)
	            	return 0;
	            else 
	            	return 1;
	            
	        }

	        public boolean equals(Object obj) {
	            return compare(this, obj) == 0;
	          }
	    });
		Enumeration receivedKeys = _inboxMessages.keys();
		while(receivedKeys.hasMoreElements())
		{
			sortedMessages.add(_inboxMessages.get(receivedKeys.nextElement()));
		}
		sortedMessages.reSort();
		LinkedHashtable unreadSubjectCounts = new LinkedHashtable();
		int totalCount=0;
		
		for(int i =0;i<sortedMessages.size();i++)
		{
			
			PMMessage message = (PMMessage)sortedMessages.elementAt(i);
			if(message instanceof PMReceivedMessage)
			{
				
				Integer currentCount = (Integer)unreadSubjectCounts.get(message.getSubject());
				
				
				if(currentCount==null)
				{
					currentCount = new Integer(0);
					unreadSubjectCounts.put(message.getSubject(), currentCount);
				}
				if(message.getStatus()!=128)
				{
					unreadSubjectCounts.put(message.getSubject(), new Integer(currentCount.intValue()+1));
					totalCount++;
				}
			}
		}
		
		unreadSubjectCounts.put(ALL_UNREAD_MESSAGES_SUBJECT, new Integer(totalCount));

		return unreadSubjectCounts;
	}
	/**
	 * removes the deleted flag from all messages in the database, thereby
	 * 'undeleting' them
	 */
	public void unmarkAll()
	{
		markMultipleAsDeleted((Vector)_deletedMessages.elements(), false);
	}


    /**
     * Completely deletes the message from the message store
     * 
     * @param message The message to delete from the message store
     */
    void deleteMessageCompletely(PMMessage message)
    {
        _deletedMessages.removeMessage(message);
        
        persistStore();
    }


    /**
     * Retrieves the inbox messages as a readable list
     * 
     * @return The readable list of all the inbox messages
     */
    public ReadableList getInboxMessages()
    {
        return _inboxMessages;
    }


    /**
     * Gets the deleted messages as a readable list
     * 
     * @return The readable list of all the deleted messages
     */
    public ReadableList getDeletedMessages()
    {
        return _deletedMessages;
    }
    

    
    static class PersistableHashtable extends LinkedHashtable implements Persistable
    {
    	
    }
    static class ReadableListImpl implements ReadableList, Persistable
    {
        private PersistableHashtable hashedMessages;

        /**
         * Creates a empty instance of ReadableListImpl
         */
        ReadableListImpl()
        {
            hashedMessages = new PersistableHashtable();
        }


        /**
         * @see net.rim.device.api.collection.ReadableList#getAt(int)
         */
        public Object getAt(int index)
        {
            return hashedMessages.getAt(index);
        }


       
        /**
         * @see net.rim.device.api.collection.ReadableList#getAt(int, int, Object[], int)
         */
        public int getAt(int index, int count, Object[] elements, int destIndex)
        {
        	int realCount =0;
            for(int i=index;i<index+count;i++)
            {
            	elements[i-count+destIndex] = hashedMessages.getAt(i);
            	realCount ++;
            }
            return realCount;
        }


        /**
         * @see net.rim.device.api.collection.ReadableList#getIndex(Object)
         */
        public int getIndex(Object element)
        {
        	if(element instanceof PMReceivedMessage)
        	{
        		return hashedMessages.indexOf(new Integer(((PMReceivedMessage)element).getReceivedMessageId()));
        	}
        	else
        	{
        		return hashedMessages.indexOf(new Integer(((PMSentMessage)element).getSentMessageId()));
        	}
        }


        /**
         * @see net.rim.device.api.collection.ReadableList#size()
         */
        public int size()
        {
            return hashedMessages.size();
        }


        /**
         * Add a message to this list
         * 
         * @param message The message to add to this list
         */
        void addMessage(PMMessage message)
        {
            if(message instanceof PMReceivedMessage)
            	hashedMessages.put(new Integer(message.getReceivedMessageId()),message);
            else
            	hashedMessages.put(new Integer(((PMSentMessage)message).getSentMessageId()),message);
        }
        

        /**
         * Removes a message from this list
         * 
         * @param message The message to remove from this list
         */
        void removeMessage(PMMessage message)
        {
            if(message instanceof PMReceivedMessage)
            	hashedMessages.remove(new Integer(message.getReceivedMessageId()));
            else
            	hashedMessages.remove(new Integer(((PMSentMessage)message).getSentMessageId()));
        }
        
        
        
        void put(Object key, Object value)
        {
        	hashedMessages.put(key, value);
        }
        
        Object get(Object key)
        {
        	return hashedMessages.get(key);
        }
        
        Enumeration keys()
        {
        	return hashedMessages.keys();
        }
        
        Enumeration elements()
        {
        	return hashedMessages.elements();
        }
    }
    
    
    /**
	 * Adds a list of messages to relevant db tables
	 * 
	 * @param messages
	 */
	public void addMessages(Vector messages)
	{
		for (int i = 0; i<messages.size();i++)
		{
			Object messageAsObject = messages.elementAt(i);
			PMMessage message = (PMMessage)messageAsObject;
			addInboxMessage(message);
		}
	      _mainFolder.fireReset();
	}

    /**
	 * returns the largest (and newest) received message id
	 * 
	 * @return latest message id
	 */
	public Integer getLastReceivedMessageId()
	{
		
		Enumeration keys = 	_inboxMessages.keys();
		Integer lastmessage = new Integer(-1);
		while(keys.hasMoreElements())
		{
			
			Integer newKey = (Integer) keys.nextElement();
			if(_inboxMessages.get(newKey) instanceof PMReceivedMessage)
			{
				if (newKey.intValue() >  lastmessage.intValue())
					lastmessage=newKey;
			}
		}

		keys = _deletedMessages.keys();
		while(keys.hasMoreElements())
		{
			Integer newKey = (Integer) keys.nextElement();
			if(_deletedMessages.get(newKey) instanceof PMReceivedMessage)
			{
				if (newKey.intValue() >  lastmessage.intValue())
					lastmessage=newKey;
			}
		}
		return lastmessage;
	}
	public void setApplicationFolders(ApplicationMessageFolder messages,
		ApplicationMessageFolder deleted) {
		_mainFolder = messages;
		_deletedFolder = deleted;
		
	}

	public void deleteMessageCache() {
		
		int test = _inboxMessages.size();
		int test2 = _deletedMessages.size();
		_inboxMessages = new ReadableListImpl();
		_deletedMessages = new ReadableListImpl();
		
		persistStore();
		
		_mainFolder.fireReset();
		_deletedFolder.fireReset();
		clearIndicator();
		
	}


	//this method is run when an action is performed on the message store from outside the app (ie. someone opens the notification area and looks at a message
	public void actionPerformed(int action, final ApplicationMessage[] messages, ApplicationMessageFolder folder)
	{
		// TODO Auto-generated method stub
		System.out.println("an action was performed on the application message folder, and the PMMessageStore received the callback!!!!!!!!");
		   PMMessageStore messageStore = PMMessageStore.getInstance();

	        
	        //***** THIS CODE DOES NOT SEEM TO WORK FOR OS 5 *****
	        
	        // Check if user opened the Message list and marked messages as not 'new'
//	        if(action == ApplicationMessageFolderListener.MESSAGES_MARKED_OLD)
//	        {
//	            // User opened the message list and viewed messages, remove the
//	            // 'notification' state from the indicator.
//	            ApplicationIndicator indicator = ApplicationIndicatorRegistry.getInstance().getApplicationIndicator();
//	            if(indicator != null)
//	            {
//	                indicator.setNotificationState(false);
//	            }
	//
//	            // No further processing
//	            return;
//	        }

	        synchronized(messageStore)
	        {
	            // Check if action was performed on multiple messages
	            if(messages.length == 1)
	            {
	                PMMessage message = (PMMessage) messages[0];

	                switch(action)
	                {
	                    case ApplicationMessageFolderListener.MESSAGE_DELETED:
	                        if(folder.getId() == PMMessageStore.MESSAGEBOX_KEY)
	                        {
	                            // Message from Inbox was deleted, update storage,
	                            // the message will go into the Deleted folder
	                            messageStore.markMessageDeleted(message,true);


	                            // Note: There is no need to fireElementRemoved(),
	                            // message was already deleted.                            
	                        }
	                        else
	                        {
	                           
	                        	
	                            // Note: There is no need to fireElementRemoved(),
	                            // message was already deleted.
	                        }
	                        break;
	                    case ApplicationMessageFolderListener.MESSAGE_MARKED_OPENED:

	                    	if(message instanceof PMReceivedMessage)
	                    		// Update message
	                    		messageStore.markMessageRead((PMReceivedMessage)message);


	                        break;
	                    case ApplicationMessageFolderListener.MESSAGE_MARKED_UNOPENED:

	                    	if(message instanceof PMReceivedMessage)
	                    		// Update message
	                    		messageStore.markMessageAsNew((PMReceivedMessage)message);

	                        break;
	                }
	            }
	            else
	            {
	                // Multiple messages were affected, optimize notifications
	                ApplicationMessageFolder resetFolder = folder;

	           
	                switch(action)
	                {
	                    case ApplicationMessageFolderListener.MESSAGE_DELETED:
	                    {    
	                    	if(folder.getId() == PMMessageStore.MESSAGEBOX_KEY)
	                        {
	                            // Update storage, the message will go
	                            // into the Deleted folder.
	                        	Vector vectorMessages = new Vector(){{this.elementData = messages;this.elementCount = this.elementData.length;}};
	                        	
	                            messageStore.markMultipleAsDeleted(vectorMessages,true);
	                            
	                        }
	                        else
	                        {
	                            // Message was deleted completely from the
	                            // Deleted folder, update storage.
	                            //messageStore.deleteMessageCompletely(message);
	                        }
	                        break;
	                    }
	                    case ApplicationMessageFolderListener.MESSAGE_MARKED_OPENED:
	                    {
	                    	Vector vectorMessages = new Vector(){{this.elementData = messages;this.elementCount = this.elementData.length;}};
	                    	messageStore.markMultipleAsRead(vectorMessages ,false);
	                    					
	                        break;
	                    }
	                    case ApplicationMessageFolderListener.MESSAGE_MARKED_UNOPENED:
	                    {
	                    	Vector vectorMessages = new Vector(){{this.elementData = messages;this.elementCount = this.elementData.length;}};
	                    	messageStore.markMultipleAsRead(vectorMessages ,true);
	                    					
	                        break;
	                    }
	                    
	                }

	            }
	        }
	}

	

}
