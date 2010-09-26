package com.asptt.plongee.resa.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;



public final class Parameters {

	private static final String BUNDLE_NAME = "com.asptt.plongee.resa.util.resaPlongee"; //$NON-NLS-1$
	
	
	private Parameters() {
	
	}
	
	
	/**
	* Return the value of the key If this key has no value, returns '!' + key +
	* '!'
	* 
	* @param key
	* @return
	*/
	
	public static String getString(String key) {
		try {
			//System.out.println("LOCALE getString : " + FacesContext.getCurrentInstance().getViewRoot().getLocale());
//			return ResourceBundle.getBundle(BUNDLE_NAME, FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString(key);
			return ResourceBundle.getBundle(BUNDLE_NAME).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	
	}
	
	
	/**
	* Return the value of the key If this key has no value, returns null
	* 
	* @param key
	* @return
	*/
	
	public static String getExactString(String key) {
		try {
			//System.out.println("LOCALE getExactString : " + FacesContext.getCurrentInstance().getViewRoot().getLocale());
//			return ResourceBundle.getBundle(BUNDLE_NAME, FacesContext.getCurrentInstance().getViewRoot().getLocale()).getString(key);
			return ResourceBundle.getBundle(BUNDLE_NAME).getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

}

