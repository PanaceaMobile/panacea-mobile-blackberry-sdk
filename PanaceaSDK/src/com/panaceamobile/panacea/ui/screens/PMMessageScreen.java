package com.panaceamobile.panacea.ui.screens;

import java.util.Vector;

import javax.microedition.lcdui.TextField;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

import com.panaceamobile.panacea.sdk.PMUtils;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.PanaceaSDK.Tag;
import com.panaceamobile.panacea.sdk.db.PMMessage;
import com.panaceamobile.panacea.sdk.db.PMMessageStore;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;
import com.panaceamobile.panacea.sdk.webservices.PMWebServiceController;
import com.panaceamobile.panacea.ui.controllers.PMDismissListener;
import com.panaceamobile.panacea.ui.controllers.PMScreenController;
import com.panaceamobile.panacea.ui.controls.MessageRow;
import com.panaceamobile.panacea.ui.controls.Row;
import com.panaceamobile.panacea.ui.controls.RowSelectionListener;

public class PMMessageScreen extends PMBaseScreen implements
		FieldChangeListener, RowSelectionListener {
	private int threadID;
	private EditField messageBox ;
	private ButtonField replyButton;
	private Vector messages;
	private MessageRow selectedRow;
	private PMDismissListener dismissListener;
	private HorizontalFieldManager end ;
	public static final int NO_CONTENT = 0;
	private VerticalFieldManager messagesManager;
	public PMMessageScreen(PMDismissListener dismissListener, int threadID)
	{
		super(Manager.NO_VERTICAL_SCROLL|Manager.NO_VERTICAL_SCROLLBAR);
		this.dismissListener = dismissListener;
		this.threadID = threadID;
		reloadContent(threadID);
	}
	
	public void reloadContent(int threadId)
	{
		Vector results = PMMessageStore.getInstance().getMessagesForThreadId(threadId);
		if(results.size()>0)
		{
			setContent(results);
			messageBox.setFocus();
		}
		else
		{
			dismissListener.dismiss(this,NO_CONTENT);
		}
	}
	
	public int getThreadId()
	{
		return threadID;
	}
	private void setContent(Vector results)
	{
		if(results.size()==0)
		{
			this.close();
		}
		deleteAll();
		
		messages = results;
		HorizontalFieldManager house = new HorizontalFieldManager(FIELD_BOTTOM|USE_ALL_HEIGHT)
		{
			protected boolean touchEvent(TouchEvent message)
		    {
		        if (TouchEvent.CLICK == message.getEvent())
		        {
	        		//Not entirely sure why but the message rows are swallowing all the touch events when they have focus. This hack here checks whether a 
	        		//click was meant for the text field or the button, and if it is then it sets the focus on those items
	        		int touchX =  message.getGlobalX(1);
	                int touchY =  message.getGlobalY(1);
	              
	                int messageBoxTop =  (Display.getHeight() - messagesManager.getHeight() )+ end.getTop()+messageBox.getTop();
	                int messageBoxBottom = messageBoxTop + messageBox.getHeight();
	                int replyButtonTop = (Display.getHeight() - messagesManager.getHeight() )+end.getTop() + replyButton.getTop();
	                int replyButtonBottom = replyButtonTop + replyButton.getHeight();

	                	 
	                if (touchX > messageBox.getLeft() && touchX < messageBox.getLeft() + messageBox.getWidth() && touchY > messageBoxTop && touchY < messageBoxBottom)
	                {
	            		messageBox.setFocus();
	                }
	                else if(touchX > replyButton.getLeft() && touchX < replyButton.getLeft() + replyButton.getWidth() && touchY > replyButtonTop && touchY < replyButtonBottom)
	                {
	                	replyButton.setFocus();
	                }
	                else 
	                {
	                	return super.touchEvent(message);
	                }
		        }
		       return super.touchEvent(message);
		    }
		};
		
		messagesManager = new VerticalFieldManager(VERTICAL_SCROLL|VERTICAL_SCROLLBAR|FIELD_BOTTOM);
		for(int i=0;i<results.size();i++)
		{
			PMMessage receivedMessage = (PMMessage) results.elementAt(i);
			
			
				MessageRow r = new MessageRow(receivedMessage);
				r.setChangeListener(this);
				r.setRowSelectionListener(this);
				messagesManager.add(r);
		}
		
		
		messageBox = new EditField()
		{
			protected void layout(int width, int height)
			{
				
			    super.layout(Display.getWidth()-150, (this.getTextLength()==0?this.getFont().getHeight(Ui.UNITS_px):PMUtils.getHeightOfString(this.getFont(), Display.getWidth()-150, this.getText())));
			    
			  
			    setExtent(Display.getWidth()-150,(this.getTextLength()==0?this.getFont().getHeight(Ui.UNITS_px): PMUtils.getHeightOfString(this.getFont(), Display.getWidth()-150, this.getText())));
			}
			protected void paint(Graphics graphics)
			{
				// default case
				if ( (getText().length() > 0))
				{
					super.paint(graphics);
					return;
				}

				// prompt if no text entered yet
				int oldColor = graphics.getColor();
				graphics.setColor(Color.LIGHTGREY);
				graphics.drawText("Send a message...", 0, 0);
				graphics.setColor(oldColor);
			}
		};
		XYEdges xyEdge = new XYEdges(2, 2, 2, 2);
		XYEdges xyEdgeColors = new XYEdges(0x00dddddd, 0x00dddddd, 0x00dddddd, 0x00dddddd);
		Border aBorder = BorderFactory.createSimpleBorder(xyEdge, xyEdgeColors, Border.STYLE_SOLID);

		messageBox.setBorder(aBorder);
		messageBox.setPadding(5, 5, 5, 5);
		messageBox.setMargin(7, 7, 7, 7);
		
		end = new HorizontalFieldManager();
		replyButton = new ButtonField("Reply",ButtonField.CONSUME_CLICK);
		replyButton.setChangeListener(this);
		replyButton.setMargin(2, 2, 2, 2);
		end.add(messageBox);
		end.add(replyButton);
		messagesManager.add(end);
		house.add(messagesManager);
		PanaceaSDK.getInstance().markThreadAsRead(threadID);
		add(house);
		
	}
	
	public void fieldChanged(Field field, int context) {
		if(context==FieldChangeListener.PROGRAMMATIC)
		return;
	
		
		if(field==replyButton && !PMUtils.isBlankOrNull(messageBox.getText()))
		{
				
			replyToLatestMessage(messageBox.getText());
			messageBox.setText("");
		}
		
		

	}
	
	protected void makeMenu(Menu menu, int instance)
	{
		super.makeMenu(menu, instance);		
		if(selectedRow!=null)
			menu.add(deleteMessageItem);	
	}
	private MenuItem deleteMessageItem = new MenuItem("Delete Message", 1, 10)
	{
		public void run()
		{
			PanaceaSDK.getInstance().markMessageDeleted(selectedRow.getMessage());
			reloadContent(threadID);
		}
	};

	private void replyToLatestMessage(String replyText)
	{
		if (PMUtils.isBlankOrNull(replyText))
			return;

		if (messages == null)
			return;

		//find latest received message
		PMReceivedMessage receivedMessage = null;
		for (int i = messages.size() - 1; i >= 0; i--)
		{
			PMMessage msg = (PMMessage) messages.elementAt(i);
			if (msg instanceof PMReceivedMessage)
			{
				receivedMessage = (PMReceivedMessage) msg;
				break;
			}
		}

		if (receivedMessage == null)
			return;

		PanaceaSDK.getInstance().sendReply(replyText, receivedMessage);

	}
	public void onPostDataSuccess(String tag, String result, int status,
			String message, String method) {
		checkLoading();
		isOfflineMode = false;


		if ((Tag.MESSAGE_RECEIVED.equals(tag)||Tag.MESSAGE_SENT.equals(tag)))
		{
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					reloadContent(threadID);
				}
			});
			
		}
		
	}

	public void rowSelected(Row selectedRow) {
		
		this.selectedRow = (MessageRow)selectedRow;
	}

	public void rowUnselected(Row unselectedRow) {
		if(this.selectedRow == unselectedRow)
			this.selectedRow = null;
		
	}

}
