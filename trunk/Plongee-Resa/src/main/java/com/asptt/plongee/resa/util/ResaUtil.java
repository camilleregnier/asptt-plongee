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
		long diffMilli = dateFin.getTime() - dateDebut.getTime();
		int nombreJour =   Long.valueOf(((diffMilli/1000) / 3600) /24).intValue(); 
		return nombreJour;
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
		System.out.println("DATE de DEBUT:"+sdf.format(dateCM));
		
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

}

