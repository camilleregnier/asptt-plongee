package com.asptt.plongee.resa.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.asptt.plongee.resa.exception.TechnicalException;
 
public class ResaUtil {
	
	static final long ONE_DAY_IN_MILLIS=86400000;
	
	public static int calculNbHeure(Date dateDuJour, Date datePlongee) throws TechnicalException {
		long diffMilli = datePlongee.getTime() - dateDuJour.getTime();
		int nombreHeure =   Long.valueOf((diffMilli/1000) / 3600).intValue(); 
		return nombreHeure;
	}
	
	public static int calculNbJour(Date dateDebut, Date dateFin) throws TechnicalException {
		GregorianCalendar gcDeb = new GregorianCalendar();
		gcDeb.setTime(dateDebut);
		GregorianCalendar gcFin = new GregorianCalendar();
		gcFin.setTime(dateFin);
		gcDeb.set(GregorianCalendar.HOUR, 0);
		gcDeb.set(GregorianCalendar.MINUTE, 0);
		gcDeb.set(GregorianCalendar.SECOND, 0);
		gcFin.set(GregorianCalendar.HOUR, 0);
		gcFin.set(GregorianCalendar.MINUTE, 0);
		gcFin.set(GregorianCalendar.SECOND, 0);
		dateDebut.setTime(gcDeb.getTimeInMillis());
		dateFin.setTime(gcFin.getTimeInMillis());
		long diffMilli = dateFin.getTime() - dateDebut.getTime();
		float nombreJour =   ((diffMilli/(float)1000) / (float)3600) /(float)24; 
		return Double.valueOf(Math.ceil(nombreJour)).intValue();
	}
	
	/**
	 * Calcul le nombre de jour entre deux dates
	 * @param dateCM
	 * @param dateDuJour
	 * @return long nombreJour
	 * @throws TechnicalException
	 */
	public static long checkDateCM(Date dateCM, Date dateDuJour) throws TechnicalException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		GregorianCalendar gcCM = new GregorianCalendar();
		gcCM.setTime(dateCM);
		dateCM.setTime(gcCM.getTimeInMillis());
//		System.out.println("DATE de DEBUT:"+sdf.format(dateCM));
		
		GregorianCalendar gcFin = new GregorianCalendar();
		gcFin.setTime(dateCM);
		gcFin.add(GregorianCalendar.YEAR, +1);
		Date dateDeFin = new Date(gcFin.getTimeInMillis());
//		System.out.println("DATE de FIN:"+sdf.format(dateDeFin));
		
		GregorianCalendar gcDuJour = new GregorianCalendar();
		gcDuJour.setTime(dateDuJour);
//		System.out.println("DATE du JOUR:"+sdf.format(dateDuJour));
		
		long nombreJour = (dateDeFin.getTime()-dateDuJour.getTime()) / ONE_DAY_IN_MILLIS;
		
		return nombreJour;

	}

	  public static String getDateString(Date e_d){
	      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	      return dateFormat.format(e_d);
	  }

          
	public static String capitalizeFirstLetter(String value) {
		if (value == null) {
			return null;
		}
		if (value.length() == 0) {
			return value;
		}
                
		StringBuilder result = new StringBuilder(value.toLowerCase());
		result.replace(0, 1, result.substring(0, 1).toUpperCase());
		return result.toString();
	}          
}

