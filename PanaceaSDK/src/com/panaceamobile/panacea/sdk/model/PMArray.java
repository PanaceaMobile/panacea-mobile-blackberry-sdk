package com.panaceamobile.panacea.sdk.model;


import java.util.Vector;

import org.json.me2.JSONArray;
import org.json.me2.JSONObject;

/**
 * Used for passing Arrays, for example when issuing requests or receiving
 * response data. <br/>
 * The value may be one of the following types: <br/>
 * <ul>
 * <li>String
 * <li>boolean
 * <li>int
 * <li>PMDictionary
 * <li>PMArray
 * </ul>
 * 
 * @author Cobi Interactive
 */
public class PMArray
{
	private static final String NO_INDEX_EX = "PMArray does not contain a value at index ";

	private Vector array = new Vector();

	/**
	 * Constructs an empty PMArray
	 */
	public PMArray()
	{
	}

	/**
	 * Construct and parse a PMArray from a JSON String.
	 * 
	 * @param json
	 *        the JSON formatted string
	 */
	public PMArray(String json) throws Exception
	{
		this(new JSONArray(json));
	}

	protected PMArray(JSONArray jsonArray) throws Exception
	{
		for (int i = 0; i < jsonArray.length(); i++)
		{
			if (jsonArray.isNull(i))
			{
				array.addElement(null);
			}
			else
			{
				Object value = jsonArray.get(i);
				if (value.getClass().equals(JSONObject.class))
				{
					PMDictionary dictionary = new PMDictionary((JSONObject) value);
					array.addElement(dictionary);
				}
				else if (value.getClass().equals(JSONArray.class))
				{
					JSONArray a = (JSONArray) value;
					array.addElement(new PMArray(a));
				}
				else
				{
					array.addElement(value);
				}
			}
		}
	}

	/**
	 * return the array's length
	 * 
	 * @return the array's length
	 */
	public int length()
	{
		return this.array.size();
	}

	public void add(String val)
	{
		array.addElement(val);
	}

	public void add(boolean val)
	{
		array.addElement(new Boolean(val));
	}

	public void add(int val)
	{
		array.addElement(new Integer(val));
	}

	public void add(long val)
	{
		array.addElement(new Long(val));
	}

	public void add(PMDictionary val)
	{
		array.addElement(val);
	}

	public void add(PMArray val)
	{
		array.addElement(val);
	}

	public void add(double val)
	{
		array.addElement(new Double(val));
	}

	public String getString(int index)
	{
		Object obj = array.elementAt(index);
		if (obj == null)
			return null;
		else
			return obj.toString();
	}

	public boolean getBool(int index)
	{
		Object obj = array.elementAt(index);
		if (obj == null)
			throw new NullPointerException(NO_INDEX_EX + index);

		try
		{
			return ((Boolean) obj).booleanValue();
		}
		catch(Exception e)
		{
			return obj.toString().toLowerCase().equals("true") || obj.toString().equals("1");
		}
	}

	public int getInt(int index)
	{
		Object obj = array.elementAt(index);
		if (obj == null)
			throw new NullPointerException(NO_INDEX_EX + index);

		try
		{
			return ((Integer) obj).intValue();
		}
		catch(Exception e)
		{
			return Integer.parseInt(getString(index));
		}
	}


	public double getDouble(int index)
	{
		Object obj = array.elementAt(index);
		if (obj == null)
			throw new NullPointerException(NO_INDEX_EX + index);

		try
		{
			return ((Double) obj).doubleValue();
		}
		catch(Exception e)
		{
			return Double.parseDouble(getString(index));
		}
	}

	public PMDictionary getObject(int index)
	{
		Object obj = array.elementAt(index);
		if (obj == null)
			return null;
		else
			return (PMDictionary) obj;
	}

	public PMArray getArray(int index)
	{
		Object obj = array.elementAt(index);
		if (obj == null)
			return null;
		else
			return (PMArray) obj;
	}

	public String toString()
	{
		return array.toString();
	}
}
