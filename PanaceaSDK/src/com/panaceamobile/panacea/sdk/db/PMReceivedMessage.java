package com.panaceamobile.panacea.sdk.db;

import java.util.Vector;

import net.rim.device.api.util.Persistable;

import com.panaceamobile.panacea.sdk.model.PMArray;
import com.panaceamobile.panacea.sdk.model.PMDictionary;

/**
 * Object representation for data saved in the database -
 * {@link PMDatabaseHelper}. Messages received from Panacea
 * 
 * @see PMMessage
 * @see PMSentMessage
 * @author Cobi Interactive
 */
public class PMReceivedMessage extends PMMessage implements Persistable
{
	public static class Status implements Persistable
	{
		public static final int ACKNOWLEDGED = 1; //Acknowledge the message send request
		public static final int SUBMITTED = 2; //Submitted to the Push Gateway (PW) 
		public static final int DELIVERED = 4; //* Don't think this is currently being used, as we don't get this back from the PW
		public static final int FAILED = 32;
		public static final int DOWNLOADED = 64; //* Not used at all, please ignore totally at this stage
		public static final int READ = 128;
	}

	private String subject;
	private int status;

	public PMReceivedMessage()
	{
	}

	public PMReceivedMessage(PMDictionary message)
	{
		super(message);
		this.setStatus(message.getInt("status", -1));
		this.setSubject(message.getString("subject", null));
	}


	public String toString()
	{
		return super.toString() + "\nPMReceivedMessage [subject=" + subject + ", status=" + status
			+ "]";
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public int getStatus()
	{
		return status;
	}

	public boolean isUnread()
	{
		return status != Status.READ;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public static Vector parseReceivedMessagesArray(PMArray messagesArray)
	{
		Vector messages = new Vector();

		for (int i = 0; i < messagesArray.length(); i++)
		{
			messages.addElement(new PMReceivedMessage(messagesArray.getObject(i)));
		}

		return messages;
	}


}
