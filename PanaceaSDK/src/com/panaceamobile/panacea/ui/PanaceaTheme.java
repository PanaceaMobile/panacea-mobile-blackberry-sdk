package com.panaceamobile.panacea.ui;

import com.panaceamobile.panacea.ui.controls.SubjectRow;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.PhoneArguments;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;


public class PanaceaTheme {
	
	protected static int ptHeight = -1;
	public static int p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
	
	// colors
	public static final int BACKGROUND_COLOUR 		= 0xf8f8f8;
	public static final int DARK_THEME_COLOUR		= 0x006150;
	public static final int THEME_COLOUR  			= 0x00A68B;
	public static final int LIGHT_GREY_COLOUR 		= 0xcccccc;
	public static final int BUTTON_GREY_COLOUR		= 0xaaaaaa;
	public static final int GREY_COLOUR 			= 0x555555;
	public static final int ABOUT_BACKGROUND_COLOUR = 0x434641;

	// old colours
	public static final int BACKGROUND_SELECTED_COLOUR = 0xd8d8d8;
	public static final int DIVIDER_COLOUR 		= 0x666666; //0xcccccc;
	public static final int BUTTON_COLOUR 		= 0x999999;
	public static final int DISABLED_GREY 		= 0x999999;
	public static final int DARK_GREY 			= 0x696969;
	
	public static final int TEXT_COLOUR 			= 0x015548;
	public static final int DARK_TEXT_COLOUR 	= 0x0a342e; 	
	public static final int HEADER_TEXT_COLOUR 	= 0x223884;
	
	public static final int TEXTFIELD_BACKGROUND = 0xffffff;
	
	// metrics
	public int ROW_HEIGHT 		= 68; 		// dp
	public int PADDING_LEFT 	= 10;		// pts	
	public int TOOLBAR_HEIGHT 	= 36;		// dp
	public int TAB_BAR_HEIGHT	= 60; 		// dp
	public int HEADER_HEIGHT	= 56; 		// dp
	
	public int TITLE_FONT_SIZE = 12;		// pts
				
	// decor
	public Background buttonBackground = null;
	public Background buttonBackgroundActive = null;	
	public Border buttonBorder = null;
	public Border buttonBorderActive = null;

	public Background tabButtonBackground = null;
	public Background tabButtonBackgroundActive = null;	
	public Border tabButtonBorder = null;
	public Border tabButtonBorderActive = null;
	
	Background headerBackground = null;
		
	
	// resources
	public String dropArrowBitmap = null; 
	public String disclosureArrow = null; 	
	public String headerBackgroundBitmap = null;
	
	public PanaceaTheme() {		
		initTheme();
	}
	
	protected void initTheme() {
		initPts();
		
		headerBackground = BackgroundFactory.createSolidBackground(DARK_TEXT_COLOUR);
		
		buttonBackground = BackgroundFactory.createSolidBackground(BUTTON_COLOUR);
		buttonBackgroundActive = BackgroundFactory.createSolidBackground(BACKGROUND_COLOUR);	
		
		tabButtonBackgroundActive = BackgroundFactory.createSolidTransparentBackground(0x000000, 0x60);				
				
		buttonBorder =  BorderFactory.createSimpleBorder (
				new XYEdges(1,1,1,1), 
				new XYEdges(0x000000, 0x000000, 0x000000, 0x000000),
				//new XYEdges(0,0,0,0),
				new XYEdges(Border.STYLE_SOLID, Border.STYLE_SOLID, Border.STYLE_SOLID, Border.STYLE_SOLID));

		buttonBorderActive =  BorderFactory.createSimpleBorder(
				new XYEdges(1,1,1,1), 
				new XYEdges(0x000000, 0x000000, 0x000000, 0x000000), 
				new XYEdges(Border.STYLE_TRANSPARENT, Border.STYLE_SOLID, Border.STYLE_SOLID, Border.STYLE_SOLID));
				
	}
	
	protected void initPts() {
		Font font = Font.getDefault().derive(Font.PLAIN, 10, Ui.UNITS_pt);
		ptHeight = font.getHeight() / 10;
		p1=pt(1);p2=pt(2);p3=pt(3);p4=pt(4);p5=pt(5);
		p6=pt(6);p7=pt(7);p8=pt(9);p9=pt(9);p10=pt(10);		
	}
	
	protected static int pt(int pts) {
		return pts*ptHeight;
	}

	public SubjectRow getSubjectRow(String subject, Integer numberOfMessages)
	{
		SubjectRow s = new SubjectRow(subject,numberOfMessages);
		s.setPaddingSize(p2);
		return s;
	}
	
	public LabelField getLabelField(String text)
	{
		LabelField l = new LabelField(text);
		l.setPadding(p2,p2,p2,p2);
		return l;
	}
	
}
