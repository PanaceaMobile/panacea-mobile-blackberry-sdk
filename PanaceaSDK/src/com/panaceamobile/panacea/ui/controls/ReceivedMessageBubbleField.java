package com.panaceamobile.panacea.ui.controls;

import com.panaceamobile.panacea.sdk.PMUtils;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;

public class ReceivedMessageBubbleField extends MessageBubbleField {
	
	private static int screenDensity = PMUtils.SCREEN_DENSITY;
	private static final int leftOffset = screenDensity==PMUtils.SCREEN_DENSITY_HIGH?40:(screenDensity==PMUtils.SCREEN_DENSITY_MEDIUM?23:10);
	private static final int rightOffset = screenDensity==PMUtils.SCREEN_DENSITY_HIGH?33:(screenDensity==PMUtils.SCREEN_DENSITY_MEDIUM?23:10);
	private static final int topOffset = screenDensity==PMUtils.SCREEN_DENSITY_HIGH?25:(screenDensity==PMUtils.SCREEN_DENSITY_MEDIUM?17:8);
	private static final int bottomOffset = screenDensity==PMUtils.SCREEN_DENSITY_HIGH?25:(screenDensity==PMUtils.SCREEN_DENSITY_MEDIUM?17:8);
	
	
	public ReceivedMessageBubbleField(String message)
	{
		super(message,PMUtils.getBestBitmapResource("img/speechbubble_grey.png"),leftOffset,rightOffset,topOffset,bottomOffset,0);
		setTextColor(Color.BLUE);
	}

}
