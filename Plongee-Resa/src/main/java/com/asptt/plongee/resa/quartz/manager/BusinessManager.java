package com.asptt.plongee.resa.quartz.manager;

import java.util.Date;
import java.util.GregorianCalendar;


import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Plongee;

public class BusinessManager {
	private PlongeeDao plongeeDao;
	
	public void setPlongeeDao(PlongeeDao plongeeDao) {
		this.plongeeDao = plongeeDao;
	}
	
	/**
	 * Le cron est parametré pour executer cette methode 
	 * tous les dimanches à 12h00
	 * on va créer les plongées des :
	 * Mercredi apres midi
	 * Jeudi soir
	 * Vendredi Aprem
	 * samedi matin et apres-midi
	 * dimanche matin
	 */
	public void runAction() throws TechnicalException {
		System.out.println("In business manager, I do the runAction") ;
		
		Plongee plongee = new Plongee();
		Date dateDuJour = new Date();
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(dateDuJour);
		// par defaut les plongées sont à 20 plongeurs et n'ont pas de niveau mini
		plongee.setNbMaxPlaces(20);
		plongee.setNiveauMinimum(null);
		plongee.setOuvertureForcee(true);
		//Plongée du MERCREDI Aprem
		gc.add(GregorianCalendar.DATE, +3);
		plongee.setType(Plongee.Type.APRES_MIDI);
		plongee.setDate(gc.getTime());
		plongeeDao.create(plongee);
		System.out.println("Plongée du MERCREDI aprem créee : "+gc.getTime().toString());
	
		//Plongée du JEUDI Soir
		gc.setTime(dateDuJour);
		gc.add(GregorianCalendar.DATE, +4);
		plongee.setType(Plongee.Type.SOIR);
		plongee.setDate(gc.getTime());
		plongeeDao.create(plongee);
		System.out.println("Plongée du JEUDI soir créee : "+gc.getTime().toString());
			
		//Plongée du VENDREDI Aprem
		gc.setTime(dateDuJour);
		gc.add(GregorianCalendar.DATE, +5);
		plongee.setType(Plongee.Type.APRES_MIDI);
		plongee.setDate(gc.getTime());
		plongeeDao.create(plongee);
		System.out.println("Plongée du VENDREDI aprem créee : "+gc.getTime().toString());

		//Plongée du SAMEDI matin
		gc.setTime(dateDuJour);
		gc.add(GregorianCalendar.DATE, +6);

		plongee.setType(Plongee.Type.MATIN);
		plongee.setDate(gc.getTime());
		plongeeDao.create(plongee);
		System.out.println("Plongée du SAMEDI matin créee : "+gc.getTime().toString());
		//Plongée du SAMEDI aprem
		plongee.setType(Plongee.Type.APRES_MIDI);
		plongee.setDate(gc.getTime());
		plongeeDao.create(plongee);
		System.out.println("Plongée du SAMEDI aprem créee : "+gc.getTime().toString());

		//Plongée du DIMANCHE matin
		gc.setTime(dateDuJour);
		gc.add(GregorianCalendar.DATE, +7);
		plongee.setType(Plongee.Type.MATIN);
		plongee.setDate(gc.getTime());
		plongeeDao.create(plongee);
		System.out.println("Plongée du DIMANCHE matin créee : "+gc.getTime().toString());
	}
}
