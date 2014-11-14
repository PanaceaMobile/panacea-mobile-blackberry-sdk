package com.panaceamobile.panacea.sdk.db;


import java.util.Vector;

import net.rim.device.api.util.Persistable;

import com.panaceamobile.panacea.sdk.model.PMArray;
import com.panaceamobile.panacea.sdk.model.PMDictionary;

/**
 * Object representation for data saved in the database -
 * {@link PMDatabaseHelper}. Messages sent to Panacea
 * 
 * @see PMMessage
 * @see PMReceivedMessage
 * @author Cobi Interactive
 */
public class PMSentMessage extends PMMessage implements Persistable
{
	private int sentMessageId;

	public PMSentMessage()
	{
	}

	public PMSentMessage(PMDictionary message)
	{
		super(message);

		this.setSentMessageId(message.getInt("inbound_message_id", -1));
	}


	public String toString()
	{
		return super.toString() + "\nPMSentMessage [sentMessageId=" + sentMessageId + "]";
	}

	public int getSentMessageId()
	{
		return sentMessageId;
	}

	public void setSentMessageId(int sentMessageId)
	{
		this.sentMessageId = sentMessageId;
	}

	public static Vector parseSentMessagesArray(PMArray messagesArray)
	{
		Vector messages = new Vector();

		for (int i = 0; i < messagesArray.length(); i++)
		{
			messages.addElement(new PMSentMessage(messagesArray.getObject(i)));
		}

		return messages;
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 150;
	}

	public String getSubject() {
		// TODO Auto-generated method stub
		return "";
	}
}
