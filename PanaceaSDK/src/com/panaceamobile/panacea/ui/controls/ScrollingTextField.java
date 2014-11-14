package com.panaceamobile.panacea.ui.controls;

import com.panaceamobile.panacea.sdk.PMUtils;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ScrollingTextField extends VerticalFieldManager {

    private TextField textField;
    private int maxHeight;
	   public ScrollingTextField(int maxHeight) {
		   
	        super(Manager.NO_VERTICAL_SCROLL);
	        this.maxHeight = maxHeight;
	        VerticalFieldManager vfm = new VerticalFieldManager(Manager.VERTICAL_SCROLL|Manager.VERTICAL_SCROLLBAR);

	        textField = new TextField() {
	            public void paint(Graphics g) {
	                getManager().invalidate();
	                super.paint(g);
	            }
	        };

	        vfm.add(textField);
	        add(vfm);
	    }

	    public void paint(Graphics g) {
	        // draw the border of the text area;
	      
	        super.paint(g);
	    }

	    public void sublayout(int width, int height) {
	      
	        super.sublayout(getRealWidth(width),getRealHeight(width));
	        setExtent(getRealWidth(width),getRealHeight(width));
	    }

	    public String getText() {
	        return textField.getText();
	    }
	    public void setText(String text) {
	        textField.setText(text);
	    }
	    private int getRealWidth(int width)
	    {
	    	int fontWidth = textField.getFont().getAdvance(textField.getText());
	    	
	    	if(fontWidth > width)
	    	{
	    		return width;
	    	}
	    	else if(fontWidth==0)
	    		return textField.getFont().getAdvance("X");
	    	else
	    		return fontWidth;
	    }
	    private int getRealHeight(int width)
	    {
	    	int fontWidth = textField.getFont().getAdvance(textField.getText());
	    	int height;
	    	if(fontWidth >width)
	    	{
	    		height = PMUtils.getHeightOfString(textField.getFont(), width, textField.getText());
	    	}
	    	else
	    	{
	    		height = textField.getFont().getHeight();
	    	}
	    	if (height < maxHeight)
    			return height;
    		else
    			return maxHeight;
	    }
	}

