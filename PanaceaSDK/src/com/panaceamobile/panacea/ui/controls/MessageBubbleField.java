package com.panaceamobile.panacea.ui.controls;

import com.panaceamobile.panacea.ui.painters.StretchBitmapBackgroundPainter;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.LabelField;

public class MessageBubbleField extends Manager {
	private Bitmap background;
	private int offsetLeft;
	private int offsetRight;
	private int offsetTop;
	private int offsetBottom;
	private StretchBitmapBackgroundPainter painter;
	private int textColor; 
	private LabelField field;
	public void setTextColor(int textColor)
	{
		this.textColor = textColor;
	}
	public Font getFont()
	{
		return field.getFont();
	}
	public void setPadding(int top, int right, int bottom, int left)
	{
		//super.setPadding(new XYEdges(top, right, bottom, left));
	}
	public MessageBubbleField(String message, Bitmap background, int leftOffset, int rightOffset, int topOffset, int botOffset,long style)
	{
		super(style);
		field = new LabelField(message);
		field.setFont(field.getFont().derive(Font.PLAIN,8,Ui.UNITS_pt));
		this.background = background;
		this.offsetLeft = leftOffset;
		this.offsetRight = rightOffset;
		this.offsetBottom = botOffset;
		this.offsetTop = topOffset;
		
		painter = new StretchBitmapBackgroundPainter(background, leftOffset, rightOffset, topOffset,botOffset,this);
		add(field);
		
	}
	/**
	 * Returns the text (or label for a {@link ButtonField}) of this field. </p>
	 * 
	 * Unsupported {@link Field} types will return <code>null</code>. Currently
	 * supports {@link ButtonField}, {@link LabelField}, {@link BasicEditField}.
	 */
	public String getText()
	{
		// null check
		if (field == null)
		{
			return null;
		}

		String text = field.getText();
		
		return text;
	}

	/**
	 * Sets the text (or label for a {@link ButtonField}) of this field. </p>
	 * 
	 * Unsupported {@link Field} types will do nothing. Currently supports
	 * {@link ButtonField}, {@link LabelField}, {@link BasicEditField}.
	 */
	public void setText(String text)
	{
		// null check
		if (field == null)
		{
			return;
		}

		
		field.setText(text);
		
		updateLayout();
	}
	protected void sublayout(int maxWidth, int maxHeight) {
		// layout child field to get height so padding can be calculated
				int bitmapHeight = background.getHeight();

				// calculate correct padding for the field to be centered
				if (field != null)
				{
					// remove padding to first find out the net size of the field
					XYEdges noPadding = new XYEdges(offsetTop, offsetRight,offsetBottom, offsetLeft);
					field.setPadding(noPadding);
					layoutChild(field, maxWidth, maxHeight);
					setPositionChild(field, 0, 0);
				}


				// set custom size according to field & bitmap
				int width;
				int height;
				if ((field == null) )
				{
					width = maxWidth;
					height = maxHeight;
				}
				else
				{
					width = field.getWidth();
					height = field.getHeight();
				}
				
				
				
				setExtent(width, height);
		
	}
	protected void paint(Graphics graphics)
	{
		// save colour on start of method in case it gets changed
		int originalColor = graphics.getColor();

		
		// draw the stretched bitmap
		painter.paintBackground(graphics);
		

		graphics.setColor(textColor);
			

		/* DRAW THE FIELD */
		super.paint(graphics);

		/* RESTORE COLOUR CHANGES */
		graphics.setColor(originalColor);
	}
}
