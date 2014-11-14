package com.panaceamobile.panacea.sdk.exception;

/**
 * The exception that is thrown when the key specified for accessing an element
 * in a collection does not match any key in the collection.
 * 
 * @author Cobi Interactive
 */

public class PMKeyNotFoundException extends PMException
{
	public PMKeyNotFoundException(String msg)
	{
		super(msg);
	}
}
