package com.asptt.plongee.resa.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.asptt.plongee.resa.exception.TechnicalException;

public class ResaUtil {

	
	public static int calculNbHeure(Date dateDuJour, Date datePlongee) throws TechnicalException {
		GregorianCalendar gcPlongee = new GregorianCalendar();
		gcPlongee.setTime(datePlongee);
		GregorianCalendar gcJour = new GregorianCalendar();
		gcJour.setTime(dateDuJour);
		
		int nbJour = gcPlongee.get(Calendar.DAY_OF_MONTH) - gcJour.get(Calendar.DAY_OF_MONTH);
		int nbHour = gcPlongee.get(Calendar.HOUR_OF_DAY) - gcJour.get(Calendar.HOUR_OF_DAY);
		
		int nombreHeure = (nbJour * 24) + nbHour;
		return nombreHeure;
	}
	
}
