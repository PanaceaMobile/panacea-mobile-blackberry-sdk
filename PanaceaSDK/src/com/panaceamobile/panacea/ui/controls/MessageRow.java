package com.panaceamobile.panacea.ui.controls;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.panaceamobile.panacea.sdk.PMUtils;
import com.panaceamobile.panacea.sdk.db.PMMessage;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;

public class MessageRow extends Row {
	private	int fontColor = Color.LIGHTGRAY;
	private PMMessage message;
	private LabelField time;
	public PMMessage getMessage()
	{
		return message;
	}
	public MessageRow(PMMessage message)
	{
		super(VerticalFieldManager.USE_ALL_WIDTH);
		this.message = message;
		MessageBubbleField f;
		if(message instanceof PMReceivedMessage)
		{
			f = new ReceivedMessageBubbleField(message.getText());
			f.setPadding(0,50,0,0);
			
			add(f);
		}
		else
		{
			f = new SentMessageBubbleField(message.getText());
			f.setPadding(0,0,0,50);
			add(f);
		}
		
		time = new LabelField(PMUtils.getRelativeDate(message.getCreated()))
		{
						
			public void paint(Graphics g)
			{
				g.setColor(fontColor);
				super.paint(g);
			}
		};
		time.setPadding(5,5,5,(Display.getWidth() - time.getFont().getAdvance(time.getText()))/2);
		add(time);
	

		
	}
	
	protected void rowSelected()
	{
		super.rowSelected();
		fontColor = Color.BLACK;
	}
	protected void rowUnselected()
	{
		super.rowUnselected();
		time.setFont(time.getFont().derive(Font.PLAIN,8,Ui.UNITS_pt));
		fontColor=Color.LIGHTGRAY;
	}
	
}
