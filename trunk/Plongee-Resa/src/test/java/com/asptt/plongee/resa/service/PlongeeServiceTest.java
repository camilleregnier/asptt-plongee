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
			Date datePlongee = sdf.parse("09/11/2010");
			GregorianCalendar gcPlongee = new GregorianCalendar();
			gcPlongee.setTime(datePlongee);
			gcPlongee.set(GregorianCalendar.HOUR_OF_DAY, 16);
			gcPlongee.set(GregorianCalendar.MINUTE, 0);
			gcPlongee.set(GregorianCalendar.SECOND, 0);
			
//			Date dateDuJour = sdf.parse("08/11/2010");
			Date dateDuJour = new Date();
			GregorianCalendar gcJour = new GregorianCalendar();
			gcJour.setTime(dateDuJour);
//			gcJour.set(GregorianCalendar.HOUR_OF_DAY, 17);
//			gcJour.set(GregorianCalendar.MINUTE, 0);
//			gcJour.set(GregorianCalendar.SECOND, 0);
			
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
}

