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


import com.asptt.plongee.resa.dao.PlongeeDaoTest;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.quartz.manager.BusinessManager;
import com.asptt.plongee.resa.util.Parameters;
import com.asptt.plongee.resa.util.ResaUtil;
public class PlongeeServiceTest extends AbstractServiceTest {
	
	private final Logger logger = Logger.getLogger(getClass());
//	@Test
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

//	@Test
	public void testUpdatePlongee() throws TechnicalException {
		Plongee plongee = new Plongee();
		plongee.setId(4);
		plongee.setNbMaxPlaces(15);
		plongee.setNiveauMinimum("P3");
		plongee.setOuvertureForcee(true);
		plongeeDao.update(plongee);
	}

//	@Test
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

//	@Test
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

//	@Test
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


//	@Test
	public void testCalculNbMois() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date dateCM = sdf.parse("15/04/2009");
			GregorianCalendar gcDebut = new GregorianCalendar();
			gcDebut.setTime(dateCM);
			dateCM.setTime(gcDebut.getTimeInMillis());
			
			Date dateDuJour = sdf.parse("03/03/2010");
			GregorianCalendar gcFin = new GregorianCalendar();
			gcFin.setTime(dateDuJour);
			dateDuJour.setTime(gcFin.getTimeInMillis());
			
			long result = ResaUtil.checkDateCM(dateCM, dateDuJour);
			System.out.println("il reste:"+result+"jour");
			

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void testDateQuartz() {
			//Plongée du JEUDI Soir
		BusinessManager bm = new BusinessManager();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date uneDate = sdf.parse("15/09/2011");
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(uneDate);
			Date datePlongeeJeudi = gc.getTime();
			int annee = gc.get(Calendar.YEAR);
			Date dateDeb = sdf.parse("31/05/"+annee);
			Date dateFin = sdf.parse("16/09/"+annee);
			//Test si on est entre le 1/6 et le 15/9
			if(dateDeb.before(datePlongeeJeudi) && datePlongeeJeudi.before(dateFin)){
				logger.info("On est bien entre le 1/6 et le 15/9");
				bm.runAction();
			} else {
				logger.info("On ne créé pas de plongée");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
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



