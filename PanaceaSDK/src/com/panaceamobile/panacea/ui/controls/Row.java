package com.panaceamobile.panacea.ui.controls;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class Row extends VerticalFieldManager {

	protected RowSelectionListener listener;
	protected int focused_color = 0xA9D6E8;
	protected int unfocused_color = Color.WHITE;
	protected int background_color=unfocused_color;
	protected int paddingSize;
	
	
	public RowSelectionListener getRowSelectionListener()
	{
		return this.listener;
	}
	
	public void setRowSelectionListener(RowSelectionListener listener)
	{
		this.listener = listener;
	}
	
	public void setPadding(int padding)
    {
	    	paddingSize = padding;
	}
	public void setFocusedColor(int color)
	{
		focused_color = color;
	}
	
	public int getFocusedColor()
	{
		return focused_color;
	}
	
	public void setUnfocusedColor(int color)
	{
		focused_color = color;
	}
	
	
	public int getUnfocusedColor()
	{
		return focused_color;
	}
	
	
	
	protected void paint(Graphics graphics) {

        graphics.setBackgroundColor(background_color);
        graphics.clear();
        invalidate();
        super.paint(graphics);
    }
   
    protected boolean navigationClick(int status, int time) {
//536870912
        if(Touchscreen.isSupported()&&status==0){
            return true;
        }else{
            fieldChangeNotify(1);
            return true;
        }

    }
    protected boolean touchEvent(TouchEvent message)
    {
        if (TouchEvent.CLICK == message.getEvent())
        {
            FieldChangeListener listener = getChangeListener();
            if(!this.isFocus())
            	this.setFocus();
            else if (null != listener)
            {
            	
        		
        		int touchX =  message.getGlobalX(1);
                int touchY =  message.getGlobalY(1);
                
                
                XYRect rc = new XYRect(0,0,1,1); 
                this.getScreen().getFocusRect(rc);
                int left;
            	int right;
            	int top;
            	int bottom;
               
                	 left = rc.x;
                	 right = left+this.getManager().getWidth();
                	 top = rc.y;//+S1Application.getRootController().getWelcomeBar().getHeight() + S1Application.getRootController().getToolBar().getHeight();
                	 bottom = top+this.getManager().getHeight();
               

                
                if (touchX > left && touchX < right && touchY > top && touchY < bottom)
                {
            		listener.fieldChanged(this, 1);
                }
                
            }
        }
        return super.touchEvent(message);
    }
    
    public Row()
    {
    	super();
    	addNullField();
    }
    
    public Row(long style)
    {
    	super(style);
    	addNullField();
    }
    private void addNullField()
    {
    	NullField n = new NullField(Field.FOCUSABLE)
		{
			 protected void onFocus(int direction) {
				 	
			        rowSelected();
			        invalidate();
			        Row row =  ((Row) this.getManager());
			        if(row.listener!=null)
			        	row.listener.rowSelected(row);
			    }
			    protected void onUnfocus() {
			        rowUnselected();invalidate();
			        Row row =  ((Row) this.getManager());
			        if(row.listener!=null)
			        	row.listener.rowUnselected(row);

			    }
		};
		
		add(n);
    }

    /**
     * Override this method to supply the row with Ui changes that should happen when the row is selected. Currently changes the background color
     */
    protected void rowSelected()
    {
        background_color=focused_color;
    }
    
    /**
     * Override this method to supply the row with Ui changes that should happen when the row is unselected. Currently changes the background color
     */
	protected void rowUnselected()
	{
        background_color=unfocused_color;
	}
}
