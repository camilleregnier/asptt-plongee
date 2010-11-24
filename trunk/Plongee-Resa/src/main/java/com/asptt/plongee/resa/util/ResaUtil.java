package com.asptt.plongee.resa.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.asptt.plongee.resa.exception.TechnicalException;

public class ResaUtil {

	
	public static int calculNbHeure(Date dateDuJour, Date datePlongee) throws TechnicalException {
//		GregorianCalendar gcPlongee = new GregorianCalendar();
//		gcPlongee.setTimeInMillis(datePlongee.getTime());
//		GregorianCalendar gcJour = new GregorianCalendar();
//		gcJour.setTimeInMillis(dateDuJour.getTime());
		long diffMilli = datePlongee.getTime() - dateDuJour.getTime();
		int nombreHeure =   Long.valueOf((diffMilli/1000) / 3600).intValue(); 
//		int nbJour = gcPlongee.get(Calendar.DAY_OF_MONTH) - gcJour.get(Calendar.DAY_OF_MONTH);
//		int nbHour = gcPlongee.get(Calendar.HOUR_OF_DAY) - gcJour.get(Calendar.HOUR_OF_DAY);
//		int nombreHeure = (nbJour * 24) + nbHour;
		return nombreHeure;
	}
	
}
