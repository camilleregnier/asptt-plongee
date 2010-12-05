package com.asptt.plongee.resa.service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowire;

import sun.util.resources.CalendarData;

import com.asptt.plongee.resa.dao.PlongeeDaoTest;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.util.Parameters;
import com.asptt.plongee.resa.util.ResaUtil;
public class PlongeeServiceTest extends AbstractServiceTest {
	
	private final Logger logger = Logger.getLogger(getClass());
	@Test
	public void testCreerPlongee() throws TechnicalException {
		
		Plongee plongee = new Plongee();
		plongee.setNbMaxPlaces(20);
		plongee.setNiveauMinimum(null);
		plongee.setOuvertureForcee(true);
		plongee.setType(Plongee.Type.MATIN);
		
		Date dateDuJour = new Date();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(dateDuJour);
		gc.add(GregorianCalendar.DATE, +3);
		
		gc.set(GregorianCalendar.HOUR_OF_DAY, 8);
		gc.set(GregorianCalendar.MINUTE, 0);
		gc.set(GregorianCalendar.SECOND, 0);
		
		plongee.setDate(gc.getTime());
		
		plongeeDao.create(plongee);
		logger.info("date + 3 jours à  8 heure: "+gc.getTime().toString());
		
	
	}

	@Test
	public void testrechercherPlongee() throws TechnicalException {
		try {
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date maDate;
		maDate = sdf.parse("19/08/2010");
		gc.setTime(maDate);
		String maDateAffichee = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE);			
		int monNumJour = gc.get(Calendar.DAY_OF_WEEK);
		List<Plongee> plongees = plongeeDao.getPlongeesWhithSameDate(maDate, "APRES_MIDI");
		logger.info("nombre de plongee trouvées = "+plongees.size());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testUpdatePlongee() throws TechnicalException {
		Plongee plongee = new Plongee();
		plongee.setId(4);
		plongee.setNbMaxPlaces(15);
		plongee.setNiveauMinimum("P3");
		plongee.setOuvertureForcee(true);
		plongeeDao.update(plongee);
	}

	@Test
	public void testRechercherPlongee() throws TechnicalException {
		try {
			GregorianCalendar gc = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date maDate;
			maDate = sdf.parse("15/08/2010");
			gc.setTime(maDate);
			String maDateAffichee = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE);			
			int monNumJour = gc.get(Calendar.DAY_OF_WEEK);
			List<Plongee> plongees =plongeeDao.getPlongeesWhithSameDate(maDate, "MATIN");
			logger.info("Nombre de plongées trouvées = "+plongees.size());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCalculNbHeure() throws TechnicalException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date datePlongee = sdf.parse("24/11/2010");
			GregorianCalendar gcPlongee = new GregorianCalendar();
			gcPlongee.setTime(datePlongee);
			gcPlongee.set(GregorianCalendar.HOUR_OF_DAY, 13);
			gcPlongee.set(GregorianCalendar.MINUTE, 0);
			gcPlongee.set(GregorianCalendar.SECOND, 0);	
			datePlongee.setTime(gcPlongee.getTimeInMillis());
			
			Date dateDuJour = sdf.parse("23/11/2010");
//			Date dateDuJour = new Date();
			GregorianCalendar gcJour = new GregorianCalendar();
			gcJour.setTime(dateDuJour);
			gcJour.set(GregorianCalendar.HOUR_OF_DAY, 11);
			gcJour.set(GregorianCalendar.MINUTE, 40);
			gcJour.set(GregorianCalendar.SECOND, 0);
			dateDuJour.setTime(gcJour.getTimeInMillis());
			
			int nbJour = gcPlongee.get(Calendar.DAY_OF_MONTH) - gcJour.get(Calendar.DAY_OF_MONTH);
			int nbHour = gcPlongee.get(Calendar.HOUR_OF_DAY) - gcJour.get(Calendar.HOUR_OF_DAY);
			
			int nombreHeure = (nbJour * 24) + nbHour;
			
			System.out.println("Jour plongee = "+gcPlongee.get(Calendar.DAY_OF_MONTH));
			System.out.println("Jour  = "+gcJour.get(Calendar.DAY_OF_MONTH));
			System.out.println("Heure plongee = "+gcPlongee.get(Calendar.HOUR_OF_DAY));
			System.out.println("Heure = "+gcJour.get(Calendar.HOUR_OF_DAY));
			System.out.println("Nombre de jour entre les 2 dates = "+nbJour);
			System.out.println("Nombre de heure = "+nbHour);
			System.out.println("Nombre d'heure entre les 2 dates = "+nombreHeure);
			
			System.out.println("Calcul avec la methode = "+ResaUtil.calculNbHeure(dateDuJour, datePlongee)+"!");
			
			if(ResaUtil.calculNbHeure(dateDuJour, datePlongee) <= Parameters.getInt("desincription.alerte")){
				System.out.println("ENVOI DU MAIL");
			}
			

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testInscrireAdherent() throws TechnicalException {
		try {
			GregorianCalendar gc = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date maDate;
			maDate = sdf.parse("15/08/2010");
			gc.setTime(maDate);
			String maDateAffichee = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE);			
			int monNumJour = gc.get(Calendar.DAY_OF_WEEK);
			List<Plongee> plongees =plongeeDao.getPlongeesWhithSameDate(maDate, "MATIN");
			logger.info("Nombre de plongées trouvées = "+plongees.size());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void testCalculNbMois() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date dateDebut = sdf.parse("15/10/2009");
			GregorianCalendar gcDebut = new GregorianCalendar();
			gcDebut.setTime(dateDebut);
			gcDebut.set(GregorianCalendar.HOUR_OF_DAY, 13);
			gcDebut.set(GregorianCalendar.MINUTE, 0);
			gcDebut.set(GregorianCalendar.SECOND, 0);	
			dateDebut.setTime(gcDebut.getTimeInMillis());
			System.out.println("DATE de DEBUT:"+sdf.format(dateDebut));
			
			Date dateFin = sdf.parse("14/10/2010");
//			Date dateDuJour = new Date();
			GregorianCalendar gcFin = new GregorianCalendar();
			gcFin.setTime(dateFin);
			gcFin.set(GregorianCalendar.HOUR_OF_DAY, 11);
			gcFin.set(GregorianCalendar.MINUTE, 40);
			gcFin.set(GregorianCalendar.SECOND, 0);
			dateFin.setTime(gcFin.getTimeInMillis());
			System.out.println("DATE de FIN:"+sdf.format(dateFin));
			
			GregorianCalendar gcDebutPlusAnnee = new GregorianCalendar();
			gcDebutPlusAnnee.setTime(dateDebut);
			gcDebutPlusAnnee.add(Calendar.YEAR, 1);
			Date dateDebutPlusAnnee = new Date();
			dateDebutPlusAnnee.setTime(gcDebutPlusAnnee.getTimeInMillis());
			System.out.println("DATE DEBUT PLUS 1 AN:"+sdf.format(dateDebutPlusAnnee));
			if(dateFin.before(dateDebutPlusAnnee)){
				GregorianCalendar gcDebutPlus11Mois = new GregorianCalendar();
				gcDebutPlus11Mois.setTime(dateDebut);
				gcDebutPlus11Mois.add(Calendar.MONTH, 11);
				Date dateDebutPlus11Mois = new Date();
				dateDebutPlus11Mois.setTime(gcDebutPlus11Mois.getTimeInMillis());
				System.out.println("DATE DEBUT PLUS 11 MOIS:"+sdf.format(dateDebutPlus11Mois));
				if(dateFin.before(dateDebutPlus11Mois)){
					System.out.println("VALIDE");
				}else{
					System.out.println("CM DANS le DERNIER MOIS");
				}
			}else{
				System.out.println("CM PERIME");
			}
			
			
//			List<Integer> result = ResaUtil.calculNbMois(dateDebut, dateFin);
//			System.out.println("Nombre de mois entre le:'"+sdf.format(dateDebut)+"' et le:'"+sdf.format(dateFin)+"' est de:"+result.get(0)+"mois et:"+result.get(1)+"jour");
			

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@Test
	public void testGC() {
		 // create a GregorianCalendar with the Pacific Daylight time zone
		 // and the current date and time
		 Calendar calendar = new GregorianCalendar();
		 Date trialTime = new Date();
		 calendar.setTime(trialTime);
	
		 // print out a bunch of interesting things
		 System.out.println("ERA: " + calendar.get(Calendar.ERA));
		 System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		 System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
		 System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
		 System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
		 System.out.println("DATE: " + calendar.get(Calendar.DATE));
		 System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
		 System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
		 System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
		 System.out.println("DAY_OF_WEEK_IN_MONTH: "
		                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		 System.out.println("----------------------------------------");
		 System.out.println("getActualMaximum DAY_OF_MONTH: " + calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		 System.out.println("getActualMaximum DAY_OF_WEEK: " + calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
		 System.out.println("getLeastMaximum DAY_OF_MONTH: " + calendar.getLeastMaximum(Calendar.DAY_OF_MONTH));
		 System.out.println("getLeastMaximum DAY_OF_WEEK: " + calendar.getLeastMaximum(Calendar.DAY_OF_WEEK));
		 System.out.println("getMaximum DAY_OF_MONTH: " + calendar.getMaximum(Calendar.DAY_OF_MONTH));
		 System.out.println("getMaximum DAY_OF_WEEK: " + calendar.getMaximum(Calendar.DAY_OF_WEEK));

		 System.out.println("getActualMinimum DAY_OF_MONTH: " + calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		 System.out.println("getActualMinimum DAY_OF_WEEK: " + calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
		 System.out.println("getGreatestMinimum DAY_OF_MONTH: " + calendar.getGreatestMinimum(Calendar.DAY_OF_MONTH));
		 System.out.println("getGreatestMinimum DAY_OF_WEEK: " + calendar.getGreatestMinimum(Calendar.DAY_OF_WEEK));
		 System.out.println("getMinimum DAY_OF_MONTH: " + calendar.getMinimum(Calendar.DAY_OF_MONTH));
		 System.out.println("getMinimum DAY_OF_WEEK: " + calendar.getMinimum(Calendar.DAY_OF_WEEK));

		 System.out.println("----------------------------------------");
		 System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
		 System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
		 System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
		 System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
		 System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
		 System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
		 System.out.println("ZONE_OFFSET: "
		                    + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000)));
		 System.out.println("DST_OFFSET: "
		                    + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000)));
	
		 System.out.println("Current Time, with hour reset to 3");
		 calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
		 calendar.set(Calendar.HOUR, 3);
		 System.out.println("ERA: " + calendar.get(Calendar.ERA));
		 System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		 System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
		 System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
		 System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
		 System.out.println("DATE: " + calendar.get(Calendar.DATE));
		 System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
		 System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
		 System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
		 System.out.println("DAY_OF_WEEK_IN_MONTH: "
		                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		 System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
		 System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
		 System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
		 System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
		 System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
		 System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
		 System.out.println("ZONE_OFFSET: "
		        + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000))); // in hours
		 System.out.println("DST_OFFSET: "
		        + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000))); // in hours
		 
	}

}



