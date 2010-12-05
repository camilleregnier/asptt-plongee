package com.asptt.plongee.resa.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.asptt.plongee.resa.exception.TechnicalException;

public class ResaUtil {

	
	public static int calculNbHeure(Date dateDuJour, Date datePlongee) throws TechnicalException {
		long diffMilli = datePlongee.getTime() - dateDuJour.getTime();
		int nombreHeure =   Long.valueOf((diffMilli/1000) / 3600).intValue(); 
		return nombreHeure;
	}
	
	/**
	 * Calcul le nombre de mois et de jour entre deux dates de la meme annee
	 * Si debut > fin retourne -1 dans mois 
	 * @param dateDebut
	 * @param dateFin
	 * @return List<Integer> mois, jour
	 * @throws TechnicalException
	 */
	public static List<Integer> calculNbMois(Date dateDebut, Date dateFin) throws TechnicalException {
//		long diffMilli = dateFin.getTime() - dateDebut.getTime();
//		int nombreMois =   Long.valueOf((diffMilli/1000) / 3600 /24 /30).intValue();
		
		int nombreMois = 0;
		int nombreJour = 0;
		
		GregorianCalendar gcDeb = new GregorianCalendar();
		gcDeb.setTimeInMillis(dateDebut.getTime());
		GregorianCalendar gcFin = new GregorianCalendar();
		gcFin.setTimeInMillis(dateFin.getTime());
		
		int anneeDeb = gcDeb.get(Calendar.YEAR);
		int moisDeb = gcDeb.get(Calendar.MONTH);
		int jourDeb = gcDeb.get(Calendar.DAY_OF_MONTH);
		int anneeFin = gcFin.get(Calendar.YEAR);
		int moisFin = gcFin.get(Calendar.MONTH);
		int jourFin = gcFin.get(Calendar.DAY_OF_MONTH);
		
		if(anneeDeb > anneeFin){
			nombreMois = -1;
			nombreMois = 0;
		} else if (anneeDeb == anneeFin){
			nombreMois = moisFin - moisDeb;
			if(nombreMois == 0){
				nombreJour= gcFin.get(Calendar.DAY_OF_WEEK)- gcDeb.get(Calendar.DAY_OF_WEEK);
			} else {
				if(jourDeb == jourFin){
					nombreJour = 0;
				} else {
					nombreJour= gcFin.get(Calendar.DAY_OF_MONTH)- gcDeb.get(Calendar.DAY_OF_MONTH);
//					int nbJoursMoisDeb = gcDeb.getActualMaximum(Calendar.DAY_OF_MONTH);
//					int resteJourdeb = nbJoursMoisDeb - gcDeb.get(Calendar.DAY_OF_MONTH );
//					nombreJour = resteJourdeb + gcFin.get(Calendar.DAY_OF_MONTH);
				}
			}
		} else if (anneeDeb < anneeFin){
			int resteAnneeDeb = 12 - moisDeb;
			int nbAnnee = (anneeFin - anneeDeb -1) * 12;
			nombreMois = nbAnnee + resteAnneeDeb + moisFin;
//			if(moisDeb == moisFin){
//				nombreJour= gcFin.get(Calendar.DAY_OF_WEEK)- gcDeb.get(Calendar.DAY_OF_WEEK);
//			}else{
//				if(jourDeb == jourFin){
//					nombreJour = 0;
//				} else {
//					nombreJour= gcFin.get(Calendar.DAY_OF_MONTH)- gcDeb.get(Calendar.DAY_OF_MONTH);
////					int nbJoursMoisDeb = gcDeb.getActualMaximum(Calendar.DAY_OF_MONTH);
////					int resteJourdeb = nbJoursMoisDeb - gcDeb.get(Calendar.DAY_OF_MONTH );
////					nombreJour = resteJourdeb + gcFin.get(Calendar.DAY_OF_MONTH);
//				}
//			}
		}
		List<Integer> result = new ArrayList<Integer>();
		result.add(nombreMois);
		result.add(nombreJour);
		return result;
	}
}
