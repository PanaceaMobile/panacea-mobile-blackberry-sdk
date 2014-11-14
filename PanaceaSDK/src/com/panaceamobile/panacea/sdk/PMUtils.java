package com.panaceamobile.panacea.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.panaceamobile.panacea.sdk.push.LinkedHashtable;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.LineReader;
import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;

/**
 * This class contains various static utility methods.
 * 
 * @author Cobi Interactive
 */
public class PMUtils
{

	public static final int SCREEN_SIZE_UNKNOWN		= 0;
	public static final int SCREEN_DENSITY_LOW 		= 1;
	public static final int SCREEN_DENSITY_MEDIUM	= 2;
	public static final int SCREEN_DENSITY_HIGH		= 4;
	public static final int SCREEN_DENSITY = getScreenDensity();

	public static final String LOW_SUFFIX			= "_ldpi";
	public static final String MEDIUM_SUFFIX		= "_mdpi";
	public static final String HIGH_SUFFIX			= "_hdpi";
	
	/**
	 * Generates a user friendly string describing date relative to now.
	 * 
	 * @param dateCreated
	 * @return string of relative date
	 */
	public static String getRelativeDate(Date dateCreated)
	{
		
		//return DateUtils.getRelativeTimeSpanString(cal.getTimeInMillis()).toString();


				long time = (System.currentTimeMillis() - dateCreated.getTime()) / 1000;
		
				if (time < 0)
				{
					return "in the future";
				}
				else if (time < 60)
				{
					return time + " second" + (time == 1 ? "" : "s") + " ago";
				}
				else if (time < 60 * 60)
				{
					int diff = (int) (time / 60);
					return diff + " minute" + (diff == 1 ? "" : "s") + " ago";
				}
				else if (time < 60 * 60 * 24)
				{
					int diff = (int) (time / 60 / 60);
					return diff + " hour" + (diff == 1 ? "" : "s") + " ago";
				}
				else if (time < 60 * 60 * 24 * 30)
				{
					int diff = (int) (time / 60 / 60 / 24);
		
					if (diff == 1)
						return "yesterday";
					else
						return diff + " days ago";
				}
				else if (time < 60 * 60 * 24 * 30 * 12)
				{
					int diff = (int) (time / 60 / 60 / 24 / 30);
					return diff + " month" + (diff == 1 ? "" : "s") + " ago";
				}
				else
				{
					int diff = (int) (time / 60 / 60 / 24 / 365);
					return diff + " year" + (diff == 1 ? "" : "s") + " ago";
				}


	}
	
	
	/**
	 * Reads in the country codes from the resources and returns a map keyed on
	 * country name
	 * 
	 * @param context
	 *        Context
	 * @return LinkedHashMap key= country name, value= country code
	 */
	public static LinkedHashtable getCountryCodes()
	{
		
		LinkedHashtable results = new LinkedHashtable();
		try
		{
			InputStream is = new PMUtils().getClass().getResourceAsStream("/country_codes.txt");

			LineReader isr = new LineReader(is);
			
			
			do
			{			
				String line = new String(isr.readLine(),"UTF-8");
				results.put(line.substring(0,line.indexOf(';')),line.substring(line.indexOf(';')+1));
			}
			while(isr.lengthUnreadData()>0);
			
		}
		catch (Exception e)
		{
			return results;
		}
		return results;
	}

	/**
	 * Converts a String of a date in the provided dateFormat into a Date object
	 * 
	 * @param dateFormat
	 *        format of the input String
	 * @param date
	 *        String representation of the date
	 * @return Date object
	 */
	public static Date stringToDate(String date)
	{
		try
		{
			Date formatter = new Date(HttpDateParser.parse(date));
			return formatter;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Converts a Date object into a String with the provided dateFormat
	 * 
	 * @param dateFormat
	 *        format of the String to be return
	 * @param date
	 *        Date object to convert to String
	 * @return string representation of the date
	 */
	public static String dateToString(String dateFormat, Date date)
	{
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);
	}

//	/**
//	 * Converts an InputStream into a String
//	 * 
//	 * @param is
//	 *        InputStream to convert
//	 * @return String representation of the InputStream
//	 */
//	public static String convertStreamToString(java.io.InputStream is)
//	{
//		if (is == null)
//			return null;
//
//		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
//		return s.hasNext() ? s.next() : "";
//	}


	/**
	 * Tests if a string is a non-null, non-empty string. This can be called to
	 * determine if the string should be displayed, or not.
	 * 
	 * @param text
	 *        String to test.
	 * @return If <code>text</code> is <code>null</code>, returns
	 *         <code>false</code>. <br>
	 *         If <code>text</code> is an empty string (""), returns
	 *         <code>false</code>. <br>
	 *         Else returns <code>true</code>.
	 */
	public static boolean isNonBlankString(String text)
	{
		// null text -> false
		if (text == null)
			return false;

		// empty text -> false
		if ("".equals(text))
			return false;

		return true;
	}

	/**
	 * Tests if a string is a blank string, or is null. This can be called to
	 * determine if the string should be displayed, or not. </p> This is exactly
	 * the opposite result to {@link #isNonBlankString(String)}.
	 * 
	 * @param text
	 *        String to test.
	 * @return If <code>text</code> is <code>null</code>, returns
	 *         <code>true</code>. <br>
	 *         If <code>text</code> is an empty string (""), returns
	 *         <code>true</code>. <br>
	 *         Else returns <code>null</code>.
	 * @see #isNonBlankString(String)
	 */
	public static boolean isBlankOrNull(String text)
	{
		return !isNonBlankString(text);
	}


	/**
	 * Validates a phone number string
	 * 
	 * @param phoneNumber
	 *        input phone number String
	 * @return true if the phone number is in a valid format, otherwise false
	 */
	public static boolean isValidPhoneNumber(String phoneNumber)
	{
		if (PMUtils.isBlankOrNull(phoneNumber))
			return false;

		for(int i = 0;i<phoneNumber.length();i++)
		{
			
			char c = phoneNumber.charAt(i);
			if(!(c>='0' && c<='9'))
			{
				return false;
			}
		}
		return true;
	}
	public static Vector splitString( String src, int delim )
    {
    	if ( src == null ) return null;
    	
    	Vector strings = new Vector();    	
    	int start = 0;
    	int voIdx = 0;
    	String tempValue = src;
  
		while (start > -1) {
		    if (tempValue.length() == 0) {
		        break;
		    }
		    voIdx = tempValue.indexOf(delim);
		    if (voIdx > -1) {
		    	strings.addElement(tempValue.substring(0, voIdx));
		        start = voIdx + 1;
		        tempValue = tempValue.substring(start);
		      
		    //Last or only element
		    } else {
		        voIdx = tempValue.lastIndexOf(delim);
		        if (voIdx == -1) {
		      	  strings.addElement(tempValue);
		        } else {
		      	  strings.addElement(tempValue.substring(voIdx + 1));
		        }
		        break;
		    }
		}
		return strings;
    }
	public static int getHeightOfString(Font font, int width, String string)
	{
		String line = "";
		Vector words = splitString(string, ' ');
		int lines=1;
		int remainingLength=0;
		for(int i =0;i<words.size();i++)
		{
			
			if(remainingLength + font.getAdvance(line+words.elementAt(i)+" ")<width)
				line+=words.elementAt(i);
			else
			{
				if(font.getAdvance((String)words.elementAt(i)+" ")>=width)
				{	
					remainingLength = font.getAdvance((String)words.elementAt(i))%width;
					lines+=((int)font.getAdvance(line+words.elementAt(i))/width);
				}
				else
				{
					remainingLength =0;
					lines++;
				}
				
				line=remainingLength + (String)words.elementAt(i);
		
			}
		}
		
		return font.getHeight(Ui.UNITS_px)*lines;
	}
	
	 /**
     * Screen density low/medium/high 
     * @return
     */
	public static int getScreenDensity() {
//		Log.d(TAG, "getScreenDensity()");
		
		int width = Display.getWidth();
		int height = Display.getHeight();
		
		if (width > height) {
			if (width > 240 && width <=400 && height > 200 && height <= 280)
				return SCREEN_DENSITY_LOW;
			if (width > 400 && width <=610 && height > 280 && height <= 400)
				return SCREEN_DENSITY_MEDIUM;
			if (width > 610 && width <=680 && height > 400 && height <= 560)
				return SCREEN_DENSITY_HIGH;
		} else {
			if (width > 200 && width <=280 && height > 260 && height <= 420)
				return SCREEN_DENSITY_LOW;
			if (width > 280 && width <=400 && height > 420 && height <= 610)
				return SCREEN_DENSITY_MEDIUM;
			if (width > 400 && width <=560 && height > 610 && height <= 920)
				return SCREEN_DENSITY_HIGH;
		}
		return SCREEN_DENSITY_MEDIUM;
	}
	
	
	/**
	 * Attempts to load a bitmap for the given screen density and orientation
	 * @param resourceFileName
	 * @return
	 */
	public static Bitmap getBestBitmapResource(String resourceFileName) {
		int pos = resourceFileName.indexOf('.');
		if (pos<0)
			return null;
		
		String name = resourceFileName.substring(0, pos);
		String ext = resourceFileName.substring(pos, resourceFileName.length());

		String defaultName = resourceFileName;
		
		String densityName = null;
		int type = PMUtils.SCREEN_DENSITY;
		if ((type & SCREEN_DENSITY_LOW) > 0) {
			densityName = name + LOW_SUFFIX;
		} else if ((type & SCREEN_DENSITY_MEDIUM) > 0) {
			densityName = name + MEDIUM_SUFFIX;
		} else if ((type & SCREEN_DENSITY_HIGH) > 0) {
			densityName = name + HIGH_SUFFIX;
		}
		
		
		densityName += ext;

		Bitmap bitmap = Bitmap.getBitmapResource(densityName);
		if (bitmap==null)
		{
			bitmap = Bitmap.getBitmapResource(defaultName);
		}
		 
		return bitmap;
	}
}
