package com.asptt.plongee.resa.service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowire;

import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
public class PlongeeServiceTest extends AbstractServiceTest {
	
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
		System.out.println("date + 3 jours à  8 heure: "+gc.getTime().toString());
		
	
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
		System.out.println("nombre de plongee trouvées = "+plongees.size());
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
		plongee.setNiveauMinimum(NiveauAutonomie.P3);
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
			System.out.println("Nombre de plongées trouvées = "+plongees.size());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
