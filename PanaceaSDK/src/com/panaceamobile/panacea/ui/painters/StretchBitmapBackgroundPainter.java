package com.panaceamobile.panacea.ui.painters;

import com.panaceamobile.panacea.sdk.PMUtils;
import com.panaceamobile.panacea.ui.controls.MessageBubbleField;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;


/**
 * {@link BackgroundPainter} implementation for a Field that uses a stretchable
 * image as the background.
 */
public class StretchBitmapBackgroundPainter implements BackgroundPainter
{

	/*
	 * DESCRIPTION:
	 * This banner background is a bitmap that gets stretched to the correct
	 * width.
	 */

	private Bitmap backgroundBitmap;
	private int leftBuffer;
	private int rightBuffer;
	private int topBuffer;
	private int bottomBuffer;
	private MessageBubbleField parentField;

	/*
	 * Measure 4 x points across the image. The left-most section that does
	 * not get stretched goes from x1 to x2. The middle section that does
	 * get stretched goes from x2 to x3. The right-most section that does
	 * not get stretched goes from x3 to x4.
	 */
	int imageX1, imageX2, imageX3, imageX4;
	int imageMiddleWidth;
	
	/*
	 * Similar to above, but with height
	 */
	int imageY1, imageY2, imageY3, imageY4;
	int imageMiddleHeight;


	public StretchBitmapBackgroundPainter(Bitmap backgroundBitmap,
		int leftBuffer, int rightBuffer,
		int topBuffer, int bottomBuffer, MessageBubbleField parentField)
	{
		this.backgroundBitmap = backgroundBitmap;
		this.leftBuffer = leftBuffer;
		this.rightBuffer = rightBuffer;
		this.topBuffer = topBuffer;
		this.bottomBuffer = bottomBuffer;
		this.parentField = parentField;

		if (this.backgroundBitmap != null)
		{
			int width = backgroundBitmap.getWidth();
			imageX1 = 0;
			imageX2 = leftBuffer;//Math.min(leftBuffer, (width / 2) - 1);
			imageX3 = width - rightBuffer;//width - Math.min(rightBuffer, (width / 2) - 1);
			imageX4 = width;

			imageMiddleWidth = imageX3 - imageX2;
			
			
			int height = backgroundBitmap.getHeight();
			imageY1 = 0;
			imageY2 = topBuffer;
			imageY3 = height - bottomBuffer;
			imageY4 = height;
			
			imageMiddleHeight = imageY3-imageY2;
		}

	}

	public int getPreferredHeight()
	{
		// null check
		if (parentField == null)
		{
			return 0;
		}

		return parentField.getPreferredHeight();
	}

	public void paintBackground(Graphics graphics)
	{
		// null check
		if (backgroundBitmap == null)
		{
			graphics.drawText("IMAGE ERROR IN "
				+ StretchBitmapBackgroundPainter.class.getName(), 0, 0);
			return;
		}

		int width = parentField.getWidth() - parentField.getMarginLeft()
			- parentField.getMarginRight() - parentField.getPaddingLeft()
			- parentField.getPaddingRight();
		String test = parentField.getText();
		int height = parentField.getHeight() - parentField.getMarginTop()
				- parentField.getMarginBottom() - parentField.getPaddingTop()
				- parentField.getPaddingBottom();

		
		/* DRAW BITMAP */

		/*
		 * Measure 4 x points across the field. The left-most section that does
		 * not get stretched goes from x1 to x2. The middle section that does
		 * get stretched goes from x2 to x3. The right-most section that does
		 * not get stretched goes from x3 to x4.
		 */
		int fieldX1, fieldX2, fieldX3, fieldX4;
		fieldX1 = 0;
		fieldX2 = leftBuffer;//Math.min(Math.min(leftBuffer, width / 2), imageX2);
		fieldX3 = width - rightBuffer;//width
			//- Math.min(Math.min(rightBuffer, width / 2), imageX4 - imageX3);
		fieldX4 = width;


		
		/*
		 * Measure 4 x points across the field. The left-most section that does
		 * not get stretched goes from x1 to x2. The middle section that does
		 * get stretched goes from x2 to x3. The right-most section that does
		 * not get stretched goes from x3 to x4.
		 */
		int fieldY1, fieldY2, fieldY3, fieldY4;
		fieldY1 = 0;
		fieldY2 = topBuffer;//Math.min(Math.min(leftBuffer, width / 2), imageX2);
		fieldY3 = height - bottomBuffer;//width
			//- Math.min(Math.min(rightBuffer, width / 2), imageX4 - imageX3);
		fieldY4 = height;
		
		
		
		
		/*
		 * HACK
		 * Sometimes the button gets cut off on the right. I don't know why, but
		 * it doesn't happen in "reasonable" cases. So I'm not fixing it.
		 */
		if ((fieldX4 - fieldX3) < (imageX4 - imageX3))
		{
			System.out.println("oh dear");
			//System.out.println("*****      Possible problem in StretchBitmapBackgroundPainter.paintBackground().");
			//System.out.println("*****      Image might be cut off on the right-hand side.");
			//System.out.println("*****      Try changing the buffer values, or image sizes.");
		}
		if((fieldY4 - fieldY3) < (imageY4-imageY3))
		{
			System.out.println("oh dear");
			
		}

		/* UNSTRETCHED SECTIONS */

		//		graphics.drawBitmap(0, 0, width, height, backgroundBitmap, 0, 0);
		// top left
		graphics
			.drawBitmap(fieldX1, fieldY1, fieldX2, fieldY2, backgroundBitmap, 0, 0);
		// top right
		graphics.drawBitmap(fieldX3, fieldY1,  rightBuffer/*fieldX4*/, fieldY2, backgroundBitmap,
			imageX3, 0);
		
		// bottom left
		graphics.drawBitmap(fieldX1, fieldY3, fieldX2, bottomBuffer, backgroundBitmap, 0, imageY3);
		// bottom right
		graphics.drawBitmap(fieldX3, fieldY3,  rightBuffer/*fieldX4*/, fieldY2, backgroundBitmap,imageX3, imageY3);


		/* STRETCH THE TOP-MIDDLE AND BOTTOM-MIDDLE SECTION HORIZONTALLY */

		int startX = fieldX2;
		int leftToPaint = fieldX3 - startX;
		while (leftToPaint >= imageMiddleWidth)
		{
			int endX = startX + imageMiddleWidth;
			
			//TOP-MIDDLE
			graphics.drawBitmap(startX, 0, imageMiddleWidth, topBuffer,
				backgroundBitmap, imageX2, 0);
			
			//BOTTOM-MIDDLE
			graphics.drawBitmap(startX, fieldY3, imageMiddleWidth, bottomBuffer,
					backgroundBitmap, imageX2, imageY3);
			
			startX = endX;
			leftToPaint = fieldX3 - startX;
		}
		
		// fill up the remainder
		graphics.drawBitmap(startX, 0, leftToPaint, topBuffer,
				backgroundBitmap, imageX2, 0);
			
		graphics.drawBitmap(startX, fieldY3, leftToPaint, bottomBuffer,
					backgroundBitmap, imageX2, imageY3);
		
		
		/* STRETCH THE CENTER-LEFT AND CENTER-RIGHT SECTION VERTICALLY */

		int startY = fieldY2;
		leftToPaint = fieldY3 - startY;
		while (leftToPaint >= imageMiddleHeight)
		{
			int endY = startY + imageMiddleHeight;
			
			//CENTER-LEFT
			graphics.drawBitmap(0, startY, leftBuffer, imageMiddleHeight,
				backgroundBitmap, 0, imageY2);
			
			//CENTER-RIGHT
			graphics.drawBitmap(fieldX3, startY, rightBuffer, imageMiddleHeight,
					backgroundBitmap, imageX3, imageY2);
			
			startY = endY;
			leftToPaint = fieldY3 - startY;
		}
		
		graphics.drawBitmap(0, startY, leftBuffer, leftToPaint,
				backgroundBitmap, 0, imageY2);
			
		graphics.drawBitmap(fieldX3, startY, rightBuffer, leftToPaint,
				backgroundBitmap, imageX3, imageY2);
		
		
		
		// fill up the remainder
		
		/* STRETCH THE MIDDLE SECTION VERTICALLY AND HORIZONTALLY*/

		int widthToPaint = fieldX3 - startX;
		startY = fieldY2;
		
		int heightToPaint = fieldY3 - startY;
		while (heightToPaint >= imageMiddleHeight)
		{
			startX = fieldX2;
			
			widthToPaint = fieldX3 - startX;
			int endY = startY + imageMiddleHeight;
			while(widthToPaint >= imageMiddleWidth)
			{
				int endX = startX + imageMiddleWidth;
				
				
				graphics.drawBitmap(startX, startY, imageMiddleWidth, imageMiddleHeight,
					backgroundBitmap, imageX2, imageY2);
				
				startX = endX;
				widthToPaint = fieldX3 - startX;
			}

			graphics.drawBitmap(startX, startY, widthToPaint, imageMiddleHeight,
				backgroundBitmap, imageX2, imageY2);
			startY = endY;
			heightToPaint = fieldY3 - startY;
			
		}
		graphics.drawBitmap(startX, startY, widthToPaint, heightToPaint,
				backgroundBitmap, imageX2, imageY2);
		
		startX = fieldX2;
		
		widthToPaint = fieldX3 - startX;
		while(widthToPaint >= imageMiddleWidth)
		{
			int endX = startX + imageMiddleWidth;
			
			
			graphics.drawBitmap(startX, startY, imageMiddleWidth, imageMiddleHeight,
				backgroundBitmap, imageX2, imageY2);
			
			startX = endX;
			widthToPaint = fieldX3 - startX;
		}
		
	}

}
