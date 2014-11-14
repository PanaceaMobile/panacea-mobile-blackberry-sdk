package com.panaceamobile.panacea.ui.controls;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.panaceamobile.panacea.sdk.PMUtils;
import com.panaceamobile.panacea.sdk.db.PMReceivedMessage;

public class ThreadRow extends Row
{
	
    private int threadId;
	public ThreadRow(PMReceivedMessage message)
	{
		
		super(VerticalFieldManager.USE_ALL_WIDTH);
		HorizontalFieldManager f = new HorizontalFieldManager();
		threadId = message.getThreadID();
		
		if(message.isUnread())
		{
			ColouredField c = new ColouredField(Color.BLUE);
			c.setPadding(7,2,2,2);
			c.setPreferredHeight(10);
			c.setPreferredWidth(10);
			f.add(c);
			f.setPadding(5,5,5,5);
		}
		else
		{
			f.setPadding(5,5,5,16);
		}
		
			
		
		LabelField l = new LabelField(message.getSubject());
		l.setPadding(2,0,2,2);
		l.setFont(l.getFont().derive(Font.BOLD));
		
		f.add(l);
		

		
	

		String period = PMUtils.getRelativeDate(message.getCreated());
		LabelField messageIndicator = new LabelField(period);
		messageIndicator.setPadding(2,0,2,10);
		
		SpacerField spacer = new SpacerField(Display.getWidth()-45- l.getFont().getAdvance(message.getSubject())-messageIndicator.getFont().getAdvance(period),1);
		f.add(spacer);
		f.add(messageIndicator);
			
		add(f);
		
		
		LabelField messageText = new LabelField(message.getText());
		messageText.setPadding(2,2,2,20);
		add(messageText);
		

		SeparatorField s = new SeparatorField()
		{
			protected void paint(Graphics g)
			{
				g.setColor(0xDBDBDB);
				super.paint(g);
			}
		};
		add(s);
	}
	
	public int getThreadId()
	{
		return threadId;
	}
	
	
}
