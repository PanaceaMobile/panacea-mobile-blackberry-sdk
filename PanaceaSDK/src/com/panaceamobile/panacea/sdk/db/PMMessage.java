package com.panaceamobile.panacea.sdk.db;

import java.util.Date;

import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.Persistable;

import com.panaceamobile.panacea.sdk.PMUtils;
import com.panaceamobile.panacea.sdk.model.PMDictionary;

/**
 * Object representation for data saved in the database -
 * {@link PMDatabaseHelper}. Base PMMessage contains shared fields for both
 * {@link PMReceivedMessage} and {@link PMSentMessage}
 * 
 * @see PMReceivedMessage
 * @see PMSentMessage
 * @author Cobi Interactive
 */
public abstract class PMMessage implements ApplicationMessage, Persistable
{
	
	private static final String CREATED_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	public static final int MESSAGE_TYPE = 521;

	//	ISO8601 formats
	//	"yyyy-MM-dd'T'HH:mm:ss.SSSZ"	2001-07-04T12:08:56.235-0700
	//	"yyyy-MM-dd'T'HH:mm:ss.SSSXXX"	2001-07-04T12:08:56.235-07:00
	//	"yyyy-MM-dd'T'HH:mm:ssZ"	    2013-12-20T08:58:28+00:00

	private static final String CREATED_ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	//	private Date age;
	private int receivedMessageId;
	private String text;
	private Long created;
	private int applicationID;
	private int deviceID;
	private int threadID;
	private int deleted;

	public PMMessage()
	{
	}

	public PMMessage(PMDictionary message)
	{
		super();

		this.setApplicationID(message.getInt("application_id", -1));
		this.setDeviceID(message.getInt("device_id", -1));
		this.setText(message.getString("data", null));
		this.setReceivedMessageId(message.getInt("outbound_message_id", -1));
		this.setThreadID(message.getInt("thread_id", -1));

		this.setCreated(message.getString("created_iso8601", null));

		//fall back to created field format
		if (this.created == null)
			this.setCreated(message.getString("created", null));
	}

	public String toString()
	{
		return "PMMessage [receivedMessageId=" + receivedMessageId + ", text=" + text
			+ ", created=" + created + ", applicationID=" + applicationID + ", deviceID="
			+ deviceID + ", threadID=" + threadID + "]";
	}

	public Date getCreated()
	{
		return new Date(created.longValue());
	}

	public int getDeleted()
	{
		return deleted;
	}
	
	public void setDeleted(int deleted)
	{
		this.deleted = deleted;
	}
	
	public String getCreatedString()
	{
		return PMUtils.dateToString(CREATED_ISO8601_FORMAT, new Date(created.longValue()));
	}

	public void setCreated(String created)
	{
		this.created = new Long(PMUtils.stringToDate(created).getTime());
	}

	public void setCreated(Date created)
	{
		this.created =new Long(created.getTime());
	}

	public int getThreadID()
	{
		return threadID;
	}

	public void setThreadID(int thread_id)
	{
		this.threadID = thread_id;
	}

	public int getReceivedMessageId()
	{
		return receivedMessageId;
	}

	public void setReceivedMessageId(int receivedMessageId)
	{
		this.receivedMessageId = receivedMessageId;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public int getApplicationID()
	{
		return applicationID;
	}

	public void setApplicationID(int applicationID)
	{
		this.applicationID = applicationID;
	}

	public int getDeviceID()
	{
		return deviceID;
	}

	public void setDeviceID(int deviceID)
	{
		this.deviceID = deviceID;
	}

	public int compareTo(PMMessage another)
	{
		return (int) (created.longValue() - another.getCreated().getTime());
	}
	
	public String getContact() {
		return "Panacea";
	}

	public Object getCookie(int cookieId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getPreviewPicture() {
		// TODO Auto-generated method stub
		return getClass().getResourceAsStream("../../../../../img/icon_notify.png");
	}

	public String getPreviewText() {
		// TODO Auto-generated method stub
		return text;
	}

	public long getTimestamp() {
		// TODO Auto-generated method stub
		return created.longValue();
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}
}
