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
	 * Calcul le nombre de mois et de jour entre deux dates de la meme annee
	 * Si debut > fin retourne -1 dans mois 
	 * @param dateCM
	 * @param dateDuJour
	 * @return List<Integer> mois, jour
	 * @throws TechnicalException
	 */
	public static List<Integer> checkDateCM(Date dateCM, Date dateDuJour) throws TechnicalException {
		
		int nombreMois = 0;
		int nombreJour = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		GregorianCalendar gcCM = new GregorianCalendar();
		gcCM.setTime(dateCM);
		dateCM.setTime(gcCM.getTimeInMillis());
		System.out.println("DATE de DEBUT:"+sdf.format(dateCM));
		
		GregorianCalendar gcFin = new GregorianCalendar();
		gcFin.setTime(dateCM);
		gcFin.add(GregorianCalendar.YEAR, +1);
		Date dateDeFin = new Date(gcFin.getTimeInMillis());
		System.out.println("DATE de FIN:"+sdf.format(dateDeFin));
		
		GregorianCalendar gcDuJour = new GregorianCalendar();
		gcDuJour.setTime(dateDuJour);
		System.out.println("DATE du JOUR:"+sdf.format(dateDuJour));
		//init d'une date + 1 an pour voir si le CM est perimé
//		GregorianCalendar gcDebutPlusAnnee = new GregorianCalendar();
//		gcDebutPlusAnnee.setTime(dateDebut);
//		gcDebutPlusAnnee.add(Calendar.YEAR, 1);
//		Date dateDebutPlusAnnee = new Date();
//		dateDebutPlusAnnee.setTime(gcDebutPlusAnnee.getTimeInMillis());
//		System.out.println("DATE DEBUT PLUS 1 AN:"+sdf.format(dateDebutPlusAnnee));

		if(dateDuJour.before(dateDeFin)){
			//Le CM est encore bon
			int moisFin = gcFin.get(GregorianCalendar.MONTH);
			int moisCourant = gcDuJour.get(GregorianCalendar.MONTH);
			int jourFin = gcFin.get(GregorianCalendar.DAY_OF_MONTH);
			int jourCourant = gcDuJour.get(GregorianCalendar.DAY_OF_MONTH);
			if(moisFin == moisCourant){
				System.out.println("CM DANS LE MEME MOIS");
				nombreMois = 0;
				nombreJour = jourFin - jourCourant;
			} else {
				System.out.println("CM MOIS M-1");
				gcFin.add(GregorianCalendar.MONTH, -1);
				moisFin = gcFin.get(GregorianCalendar.MONTH);
				if(moisCourant == moisFin){
					nombreMois = 1;
					int nbJourCourant = gcDuJour.getMaximum(Calendar.DAY_OF_MONTH) - jourCourant;
					nombreJour = nbJourCourant +  jourFin;
				} else {
					System.out.println("CM LARGEMENT VALABLE");
					nombreJour = 99;
					nombreMois = 2;
				}
			}
		}else{
			System.out.println("CM PERIME");
			nombreJour = -1;
			nombreMois = -1;
		}
		System.out.println("RESTE nombreMois="+nombreMois+", nombreJour="+nombreJour+".");
		
		List<Integer> result = new ArrayList<Integer>();
		result.add(nombreMois);
		result.add(nombreJour);
		return result;
	}

}

