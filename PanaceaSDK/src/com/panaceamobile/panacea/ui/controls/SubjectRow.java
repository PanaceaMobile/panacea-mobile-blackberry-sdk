package com.panaceamobile.panacea.ui.controls;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class SubjectRow extends Row
{
	protected String subject;
	private int paddingSize;
	private HorizontalFieldManager frame;
	private ColouredField colouredField;
	private LabelField subjectLabel;
	private LabelField messageIndicator;
	private SpacerField spacer;
	
		
	public String getSubject()
	{
		return this.subject;
	}
	
	public SubjectRow()
    {
    	super();
    }
    
    public SubjectRow(long style)
    {
    	super(style);
    }
    /**
     * @param padding
     */
    public void setPaddingSize(int padding)
    {

    	frame.setPadding(padding,padding,padding,padding);
    	colouredField.setPadding(padding/2,padding/2,padding/2,padding/2);
    	subjectLabel.setPadding(padding/2,0,padding/2,padding/2);
    	if(messageIndicator!=null)
    	{	
    		messageIndicator.setPadding(padding/2,0,padding/2,padding/2);
    		spacer.setWidth(Display.getWidth()-padding*11- subjectLabel.getFont().getAdvance(subject)-messageIndicator.getFont().getAdvance(messageIndicator.getText()));
    	}
    	
    }
	public SubjectRow(String subjectName, Integer numberOfMessages)
	{
		super(VerticalFieldManager.FOCUSABLE);
		this.subject = subjectName;
		frame = new HorizontalFieldManager();
		frame.setPadding(5,5,5,5);
		
		colouredField = new ColouredField(subjectName.hashCode());
		colouredField.setPadding(2,2,2,2);
		subjectLabel = new LabelField(subjectName);
		subjectLabel.setPadding(2,0,2,2);
		frame.add(colouredField);
		frame.add(subjectLabel);
		
		
		
	

		if(numberOfMessages.intValue()>0)
		{
			messageIndicator = new LabelField(numberOfMessages.toString());
			
			messageIndicator.setPadding(2,0,2,2);
			spacer = new SpacerField(Display.getWidth()-45- subjectLabel.getFont().getAdvance(subjectName)-messageIndicator.getFont().getAdvance(numberOfMessages.toString()),1);
			frame.add(spacer);
			frame.add(messageIndicator);
		}	
		add(frame);
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
	
	
	
}
