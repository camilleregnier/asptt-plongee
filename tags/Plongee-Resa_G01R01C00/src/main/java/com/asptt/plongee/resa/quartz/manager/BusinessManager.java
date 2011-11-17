package com.asptt.plongee.resa.quartz.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.log4j.Logger;


import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.quartz.job.QuartzJob;
import com.asptt.plongee.resa.service.PlongeeService;

public class BusinessManager {

	private PlongeeService plongeeService;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	public void setPlongeeService(PlongeeService plongeeService) {
		this.plongeeService = plongeeService;
	}
	
	/**
	 * Le cron est parametré pour executer cette methode 
	 * tous les dimanches à 12h00
	 * on va créer les plongées (pour la semaine prochaine) des :
	 * Mercredi apres midi
	 * Jeudi soir
	 * Vendredi Aprem
	 * samedi matin et apres-midi
	 * dimanche matin
	 */
	public void runAction() throws TechnicalException {
		logger.info("Classe BusinessManager : execution du runAction()");
		
		Date dateDuJour = new Date();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(dateDuJour);
		Plongee plongee = new Plongee();
		
		try {
			//------------------ +8 jours-------------------------------------------
			// par defaut les plongées sont à 20 plongeurs et n'ont pas de niveau mini
//			plongee.setNbMaxPlaces(20);
//			//plongee.setNiveauMinimum(null);
//			plongee.setOuvertureForcee(true);
//			//Plongée du MERCREDI Aprem
//			gc.add(GregorianCalendar.DATE, +3);
//			plongee.setType(Plongee.Type.APRES_MIDI);
//			plongee.setDate(gc.getTime());
//			plongeeService.creerPlongee(plongee);
//			logger.info("Plongée du MERCREDI aprem créee : "+gc.getTime().toString());
//		
//			//Plongée du JEUDI Soir
////			gc.setTime(dateDuJour);
////			gc.add(GregorianCalendar.DATE, +4);
////			plongee.setType(Plongee.Type.SOIR);
////			plongee.setDate(gc.getTime());
////			plongeeService.creerPlongee(plongee);
////			logger.info("Plongée du JEUDI soir créee : "+gc.getTime().toString());
//				
//			//Plongée du VENDREDI Aprem
//			gc.setTime(dateDuJour);
//			gc.add(GregorianCalendar.DATE, +5);
//			plongee.setType(Plongee.Type.APRES_MIDI);
//			plongee.setDate(gc.getTime());
//			plongeeService.creerPlongee(plongee);
//			logger.info("Plongée du VENDREDI aprem créee : "+gc.getTime().toString());
//	
//			//Plongée du SAMEDI matin
//			gc.setTime(dateDuJour);
//			gc.add(GregorianCalendar.DATE, +6);
//	
//			plongee.setType(Plongee.Type.MATIN);
//			plongee.setDate(gc.getTime());
//			plongeeService.creerPlongee(plongee);
//			logger.info("Plongée du SAMEDI matin créee : "+gc.getTime().toString());
//			//Plongée du SAMEDI aprem
//			plongee.setType(Plongee.Type.APRES_MIDI);
//			plongee.setDate(gc.getTime());
//			plongeeService.creerPlongee(plongee);
//			logger.info("Plongée du SAMEDI aprem créee : "+gc.getTime().toString());
//	
//			//Plongée du DIMANCHE matin
//			gc.setTime(dateDuJour);
//			gc.add(GregorianCalendar.DATE, +7);
//			plongee.setType(Plongee.Type.MATIN);
//			plongee.setDate(gc.getTime());
//			plongeeService.creerPlongee(plongee);
//			logger.info("Plongée du DIMANCHE matin créee : "+gc.getTime().toString());
	
			/**
			 * ------------------ +15 jours-------------------------------------------
			 * Le constructeur Plongee initialise les plongées à 
			 * 20 plongeurs 
			 * BATM
			 * Ouverture Forcée à true
			 */
			
			//Plongée du MERCREDI Aprem
			gc.setTime(dateDuJour);
			gc.add(GregorianCalendar.DATE, +10);
			plongee = new Plongee();
			plongee.setType(Plongee.Type.APRES_MIDI);
			plongee.setDate(gc.getTime());
			plongee.setDateVisible(null);
			plongeeService.creerPlongee(plongee);
			logger.info("Plongée du MERCREDI aprem créee : "+gc.getTime().toString());

			//Plongée du JEUDI Soir
			gc.setTime(dateDuJour);
			gc.add(GregorianCalendar.DATE, +11);
			plongee = new Plongee();
			Date datePlongeeJeudi = gc.getTime();
			int annee = gc.get(Calendar.YEAR);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date dateDeb = sdf.parse("31/05/"+annee);
				Date dateFin = sdf.parse("02/09/"+annee);
				//Test si on est entre le 1/6 et le 01/9
				if(dateDeb.before(datePlongeeJeudi) && datePlongeeJeudi.before(dateFin)){
					plongee.setType(Plongee.Type.SOIR);
					plongee.setDate(datePlongeeJeudi);
					plongee.setDateVisible(null);
					plongee.setNiveauMinimum(NiveauAutonomie.P1.toString());
					plongeeService.creerPlongee(plongee);
					logger.info("Plongée du JEUDI soir créee : "+gc.getTime().toString());
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			//Plongée du VENDREDI Aprem
			gc.setTime(dateDuJour);
			gc.add(GregorianCalendar.DATE, +12);
			plongee = new Plongee();
			plongee.setType(Plongee.Type.APRES_MIDI);
			plongee.setDate(gc.getTime());
			plongee.setDateVisible(null);
			plongeeService.creerPlongee(plongee);
			logger.info("Plongée du VENDREDI aprem créee : "+gc.getTime().toString());

			//Plongée du SAMEDI matin
			gc.setTime(dateDuJour);
			gc.add(GregorianCalendar.DATE, +13);
			plongee = new Plongee();
			plongee.setType(Plongee.Type.MATIN);
			plongee.setDate(gc.getTime());
			plongee.setDateVisible(null);
			plongeeService.creerPlongee(plongee);
			logger.info("Plongée du SAMEDI matin créee : "+gc.getTime().toString());
			//Plongée du SAMEDI aprem
			plongee = new Plongee();
			plongee.setType(Plongee.Type.APRES_MIDI);
			plongee.setDate(gc.getTime());
			plongee.setDateVisible(null);
			plongeeService.creerPlongee(plongee);
			logger.info("Plongée du SAMEDI aprem créee : "+gc.getTime().toString());

			//Plongée du DIMANCHE matin
			gc.setTime(dateDuJour);
			gc.add(GregorianCalendar.DATE, +14);
			plongee = new Plongee();
			plongee.setType(Plongee.Type.MATIN);
			plongee.setDate(gc.getTime());
			plongee.setDateVisible(null);
			plongee.setNiveauMinimum(NiveauAutonomie.P1.toString());
			plongeeService.creerPlongee(plongee);
			logger.info("Plongée du DIMANCHE matin créee : "+gc.getTime().toString());
		} catch (ResaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			plongee = null;
		}
	}
}
