package com.asptt.plongee.resa.mail;

import java.util.MissingResourceException;
import java.util.ResourceBundle;



public final class ParametersMail {

	private static final String BUNDLE_NAME = "com.asptt.plongee.resa.mail.PlongeeMail"; //$NON-NLS-1$
	
	
	private ParametersMail() {
	
	}
	
	
	/**
	* Return the value of the key If this key has no value, returns '!' + key +
	* '!'
	* @param key
	* @return
	*/
	public static String getString(String key) {
		try {
			return ResourceBundle.getBundle(BUNDLE_NAME).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	
	}
	
	
	/**
	* Return the value of the key If this key has no value, returns null
	* @param key
	* @return
	*/
	public static String getExactString(String key) {
		try {
			return ResourceBundle.getBundle(BUNDLE_NAME).getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	* Return the value of the key If this key has no value, returns -1
	* @param key
	* @return
	*/
	public static int getInt(String key) {
		try {
			String sKey = ResourceBundle.getBundle(BUNDLE_NAME).getString(key);
			return new Integer(sKey).intValue();
		} catch (MissingResourceException e) {
			return -1;
		}
	
	}

}

