package com.panaceamobile.panacea.ui.controls;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class ColouredField extends Field
{
	int colour;
	public ColouredField(int colour)
	{
		this.colour = colour;
	}
	private int myWidth = 25;
	private int myHeight = 25;

	public void setPreferredWidth(int width)
	{
		this.myWidth = width;
	}
	
	public void setPreferredHeight(int height)
	{
		this.myHeight = height;
	}
	
	public int getPreferredWidth() {
	    return myWidth;
	}

	public int getPreferredHeight() {
	    return myHeight;
	}

	protected void layout(int width, int height) {              
	    setExtent(myWidth, myHeight);
	}

	protected void paint(Graphics g) {
	   
	    g.setColor(colour);
	    g.fillRoundRect(0, 0, myWidth, myHeight, 10, 10); 
	    }
	
}


