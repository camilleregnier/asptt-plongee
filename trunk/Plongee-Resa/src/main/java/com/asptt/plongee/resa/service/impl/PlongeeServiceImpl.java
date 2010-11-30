package com.asptt.plongee.resa.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.mail.MessagingException;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.Adherent.Encadrement;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.util.Parameters;
import com.asptt.plongee.resa.util.ResaUtil;

public class PlongeeServiceImpl implements PlongeeService, Serializable {

	private static final long serialVersionUID = -8826356175277601403L;
	private PlongeeDao plongeeDao;
	private AdherentDao adherentDao;
	
	public void setPlongeeDao(PlongeeDao plongeeDao) {
		this.plongeeDao = plongeeDao;
	}

	public void setAdherentDao(AdherentDao adherentDao) {
		this.adherentDao = adherentDao;
	}

	@Override
	public void creerPlongee(Plongee plongee) throws ResaException,TechnicalException {
		//On verifie d'abord qu'elle n'existe pas déjà
		List<Plongee> plongees = rechercherPlongees(plongee.getDate(), plongee.getType());
		if(plongees.size() == 0){
			//maj de l'heure de la plongée en fonction du type
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(plongee.getDate());
			gc.set(GregorianCalendar.MINUTE, 0);
			gc.set(GregorianCalendar.SECOND, 0);
			if( plongee.getType().equalsIgnoreCase(Plongee.Type.MATIN.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 8);
				plongee.setDate(gc.getTime());
			} else if( plongee.getType().equalsIgnoreCase(Plongee.Type.APRES_MIDI.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 13);
				plongee.setDate(gc.getTime());
			} else if( plongee.getType().equalsIgnoreCase(Plongee.Type.SOIR.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 18);
				plongee.setDate(gc.getTime());
			} else if( plongee.getType().equalsIgnoreCase(Plongee.Type.NUIT.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 21);
				plongee.setDate(gc.getTime());
			}
			
			if(null == plongee.getDateVisible()){
				//Maj de la date de visibile de la plongée
				GregorianCalendar gcVisi = new GregorianCalendar();
				gcVisi.setTime(plongee.getDate());
				gcVisi.set(GregorianCalendar.HOUR_OF_DAY, 0);
				gcVisi.set(GregorianCalendar.MINUTE, 0);
				gcVisi.set(GregorianCalendar.SECOND, 0);
				
				switch (gcVisi.get(GregorianCalendar.DAY_OF_WEEK)) {
				case GregorianCalendar.SUNDAY:	//Dimanche visible j-3
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.dimanche"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				case GregorianCalendar.MONDAY:	//Lundi visible j-3
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.lundi"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				case GregorianCalendar.TUESDAY:	//Mardi visible j-3
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.mardi"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				case GregorianCalendar.WEDNESDAY:	//Mercredi visible j-3
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.mercredi"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				case GregorianCalendar.THURSDAY:	//Jeudi visible j-1
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.jeudi"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				case GregorianCalendar.FRIDAY:	//Jeudi visible j-1
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.vendredi"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				case  GregorianCalendar.SATURDAY:	//Jeudi visible j-1
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.samedi"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				default:
					//j-3
					gcVisi.add(GregorianCalendar.DAY_OF_WEEK, Parameters.getInt("visible.defaut"));
					plongee.setDateVisible(gcVisi.getTime());
					break;
				}
			}
			if(plongee.getDate().before(plongee.getDateVisible())){
				throw new ResaException("La date d'inscription doit être antérieure à la date de la plongée");
			}
			plongeeDao.create(plongee);
		}
		else{
			throw new ResaException("Cette Plongée existe déjà");
		}
	}

	@Override
	public void supprimerPlongee(Plongee plongee) throws TechnicalException {
		plongeeDao.delete(plongee);
	}

	@Override
	public void modifierPlongee(Plongee plongee) throws TechnicalException{
		plongeeDao.update(plongee);
	}

	public Plongee rechercherPlongeeParId(Integer id) throws TechnicalException{
		return plongeeDao.findById(id);
	}

	public List<Plongee> rechercherPlongeeTout()  throws TechnicalException{
		return plongeeDao.findAll();
	}

	public List<Plongee> rechercherPlongeeProchainJour(Adherent adherent)  throws TechnicalException{

		List<Plongee> plongees = new ArrayList<Plongee>();
		
		if(adherent.isVesteRouge()){
			plongees = rechercherPlongeePourEncadrant();
		} else {
			plongees = rechercherPlongeePourAdherent();
		}
		return plongees;
			
	}

	public List<Plongee> rechercherPlongeePourAdherent()  throws TechnicalException{

		List<Plongee> plongees = new ArrayList<Plongee>();

		//Appel au service DAO
		List<Plongee> plongeeTrouvees = plongeeDao.getPlongeesForAdherent();
		for(Plongee plongee: plongeeTrouvees){
			if(isOuverte(plongee)){
				plongees.add(plongee);
			}
		}
		return plongees;
			
	}

	public List<Plongee> rechercherPlongeePourEncadrant()  throws TechnicalException{

		List<Plongee> plongees = new ArrayList<Plongee>();
		
		//Appel au service DAO
		List<Plongee> plongeeTrouvees = plongeeDao.getPlongeesForEncadrant(
				Parameters.getInt("visible.apres.encadrant"),
				Parameters.getInt("visible.max"));
		
		for(Plongee plongee: plongeeTrouvees){
			plongees.add(plongee);
		}
		return plongees;
			
	}

	public List<Plongee> rechercherPlongeeAOuvrir(List<Plongee> plongees) throws TechnicalException{
		
		List<Plongee> plongeesFermees = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(!isOuverte(plongee)){
				plongeesFermees.add(plongee);
			}
		}
		return plongeesFermees;
	}

	public List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees) throws TechnicalException{
		
		List<Plongee> plongeesOuvertes = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(isOuverte(plongee)){
				plongeesOuvertes.add(plongee);
			}
		}
		return plongeesOuvertes;
	}

	/**
	 * Si l'adherent n'est pas déjà inscrit dans la liste
	 * de plongées passée en parametre
	 */
	public List<Plongee> rechercherPlongeePourInscriptionAdherent(Adherent adherent) throws TechnicalException{
		
		List<Plongee> plongeesForAdherent = new ArrayList<Plongee>();
		
		List<Plongee> plongees = new ArrayList<Plongee>();
		if(adherent.isVesteRouge()){
			plongees = rechercherPlongeePourEncadrant();
		} else {
			plongees = rechercherPlongeePourAdherent();
		}
		
		for (Plongee plongee : plongees) {
			boolean isNotInscrit = true;
			for (Adherent adh : plongee.getParticipants()) {
				if (adh.getNumeroLicense().equalsIgnoreCase(
						adherent.getNumeroLicense())) {
					isNotInscrit = false;
					break;
				}
			}
			for (Adherent adh : plongee.getParticipantsEnAttente()) {
				if (adh.getNumeroLicense().equalsIgnoreCase(
						adherent.getNumeroLicense())) {
					isNotInscrit = false;
					break;
				}
			}
			if (isNotInscrit) {
						plongeesForAdherent.add(plongee);
			}	
		}

		return plongeesForAdherent;
	}

	public List<Plongee> rechercherPlongeesAdherentInscrit(Adherent adherent, int nbHours)  throws TechnicalException{
		return plongeeDao.getPlongeesWhereAdherentIsInscrit(adherent, nbHours);
	}

	public List<Plongee> rechercherPlongeesOuvertesWithAttente(
			List<Plongee> plongees) throws TechnicalException{
		
		List<Plongee> plongeesAttente = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(isOuverte(plongee)){
				if( plongee.getParticipantsEnAttente().size() > 0 && plongee.getParticipants().size() < plongee.getNbMaxPlaces()){
					plongeesAttente.add(plongee);
				}
			}
		}
		return plongeesAttente;
	}
	
	@Override
	public List<Plongee> rechercherPlongees(Date date, String type)  throws TechnicalException{
		return plongeeDao.getPlongeesWhithSameDate(date, type);
	}

	public List<Adherent> rechercherInscriptions(Plongee plongee,String niveauPlongeur, String niveauEncadrement, String trie)  throws TechnicalException{
		return adherentDao.getAdherentsInscrits(plongee,null,null,trie);
	}

	public List<Adherent> rechercherListeAttente(Plongee plongee)  throws TechnicalException{
		return adherentDao.getAdherentsWaiting(plongee);
	}
	
	public Integer getNbPlaceRestante(Plongee plongee)  throws TechnicalException{
		Integer nbPlace =  plongee.getNbMaxPlaces() - adherentDao.getAdherentsInscrits(plongee,null,null,null).size();
		return nbPlace;
	}

	public boolean isEnoughEncadrant(Plongee plongee) throws  TechnicalException {
		boolean isOk = true;
		List<Adherent> encadrants = adherentDao.getAdherentsInscrits(plongee, null, "TOUS", null);
		float nbEncadrant = encadrants.size();
		
		if (nbEncadrant <= 1){
			//C'est le dernier encadrant => MAIL
			isOk = false;
			return isOk;
		} else {
			// il en reste au moins 1...
			List<Adherent> plongeursP0 = adherentDao.getAdherentsInscrits(plongee, "P0", null, null);
			int nbP0 = plongeursP0.size();
			List<Adherent> plongeursP1 = adherentDao.getAdherentsInscrits(plongee, "P1", null, null);
			int nbP1 = plongeursP1.size();
			List<Adherent> plongeursBATM = adherentDao.getAdherentsInscrits(plongee, "BATM", null, null);
			int nbBATM = plongeursBATM.size();
			
			//cas pour les P0, P1
			if (nbP0 + nbP1 > 0){
				float res = ((nbP0 + nbP1)) / (nbEncadrant - 1);
				// max 4 P0 ou P1 par encadrant
				if (res > 4){
					// Pas assez d'encadrant : envoie d'un mail
					isOk = false;
					return isOk;
				}
			}
			//cas pour les BATM
			if (nbBATM > 0){
				float res = (nbBATM) / (nbEncadrant - 1);
				// max 1 bapteme par encadrant
				if (res > 1){
					// Pas assez d'encadrant : liste d'attente
					isOk = false;
					return isOk;
				}
			}
			return isOk;
		}
	}

	/**
	 * retourne
	 * 1 si ok
	 * 
	 * 0 pour liste d'attente sans mail car plongée complète
	 * 2 plongeur est DP et Pilote => ouvrir plongee
	 * 3 inscription d'un encadrant ou P4 à une plongée fermée => envoi de mail à l'admin
	 * 4 inscription en liste d'attente avec mail : pas assez d'encadrant
	 * 5 inscription en liste d'attente avec mail : Liste d'attente déjà ouverte
	 * 
	 * -1 si ko
	 */
	public int isOkForResa(Plongee plongee, Adherent adherent) throws ResaException, TechnicalException {

		//initialisation du retour par defaut à 1 CàD : on inscrit
		int isOk = 1;
		
		// verifier le nombre d'inscrit
		if(getNbPlaceRestante(plongee) <= 0){
			// trop de monde : inscription en liste d'attente avec msg 'bateau plein'
			isOk = 0;
			return isOk;
		}

		//Test abandonné on inscrit en liste d'attente dès qu'il y en a une
//		if(plongee.getParticipants().size() < plongee.getNbMaxPlaces() 
//				&& ((plongee.getParticipants().size() + plongee.getParticipantsEnAttente().size() ) >= plongee.getNbMaxPlaces())){
//			// il y a des gens en liste d'attente, mais reste plus de place sur le bateau ==> fermée
//			throw new ResaException("Pas glop");
//		}
		
		//Si il y a des gens en liste d'attente on empile dans la liste d'attente
		if(plongee.getParticipantsEnAttente().size() > 0){
			// il y a des gens en liste d'attente, 
			isOk = 5;
			return isOk;
		}
		
		// SI encadrant veux reserver une plongée pas encore ouverte:
		// si DP + Pilote > le brancher sur ouvrirplongee
		// sinon > impossible
		if( ! isOuverte(plongee)){
			if(adherent.isDp() && adherent.isPilote()){
				// ouvrir plongée
				isOk = 2;
			} else if(adherent.isVesteRouge()){
				// Encadrant ou DP ou Pilote : il peut s'inscrire à la plongée même si elle est fermée
				isOk = 3;
			} else {
				// pas de assez de compétences pour ouvrir la plongée : pas inscrit !
				throw new ResaException("Inscription impossible sur cette plongée : Cette plongée n'est pas ouverte");
			}
			return isOk;
		}
		
		// Si DP = P5 et pas encadrant (plus que E2) => pas de BATM ou de P0
		String niveauDP = (plongee.getDp().getEncadrement() != null && !plongee.getDp().getEncadrement().equals(Encadrement.E2)) ? plongee.getDp().getEncadrement() : plongee.getDp().getNiveau();
		if(niveauDP.equalsIgnoreCase("P5")){
			if(adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.BATM.toString()) 
				|| adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.P0.toString())){
				// inscription refusée
				throw new ResaException("Inscription impossible sur cette plongée : Les BATM ou P0 ne sont pas admis avec un DP P5");
			}
		}
		
		// verifier le niveau mini
		int niveauAdherent = -1;
		if( ! adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.BATM.toString()) ){
			niveauAdherent = new Integer(adherent.getNiveau().substring(1)).intValue(); 
		}
		
		// verifier le niveau mini de la plongée
		int niveauMinPlongee = -1;
		if (!plongee.getEnumNiveauMinimum().equals(NiveauAutonomie.BATM)){
			niveauMinPlongee = new Integer(plongee.getNiveauMinimum().toString().substring(1)).intValue();
		}
		if(niveauAdherent < niveauMinPlongee){
			// niveau mini requis : inscription refusée
			throw new ResaException("Inscription impossible sur cette plongée : Niveau insuffisant");
		}

		//On inscrit pas qqlun si il est dejà en liste d'attente
		List<Adherent> enAttente = adherentDao.getAdherentsWaiting(plongee);
		for(Adherent attente : enAttente){
			if(attente.getNumeroLicense().equalsIgnoreCase(adherent.getNumeroLicense())){
				throw new ResaException("Inscription impossible sur cette plongée : Vous etes déjà en liste d'attente.");
			}
		}

		List<Adherent> encadrants = adherentDao.getAdherentsInscrits(plongee, null, "TOUS", null);
		float nbEncadrant = encadrants.size();
		List<Adherent> plongeursP0 = adherentDao.getAdherentsInscrits(plongee, "P0", null, null);
		int nbP0 = plongeursP0.size();
		List<Adherent> plongeursP1 = adherentDao.getAdherentsInscrits(plongee, "P1", null, null);
		int nbP1 = plongeursP1.size();
		List<Adherent> plongeursBATM = adherentDao.getAdherentsInscrits(plongee, "BATM", null, null);
		int nbBATM = plongeursBATM.size();
		
		
		// Calcul du nombre d'encadrant
		// Pour les plongeurs P0, P1
		if (niveauAdherent >= 0 && niveauAdherent < 2){
			float res = ((nbP0 + nbP1) + 1) / nbEncadrant;
			// max 4 P0 ou P1 par encadrant
			if (res > 4){
				// Pas assez d'encadrant : liste d'attente avec envoi de mail
				isOk = 4;
				return isOk;
			}
		}
		//Pour les BATM
		if (niveauAdherent < 0){
			float res = (nbBATM + 1) / nbEncadrant;
			// max 1 bapteme par encadrant
			if (res > 1){
				// Pas assez d'encadrant : liste d'attente avec envoi de mail
				isOk = 4;
				return isOk;
			}
		}
		
		// SI on est arrivé jusqu'ici : c'est bon => on inscrit
		isOk = 1;
		return isOk;

	}

	public boolean isOkForListeAttente(Plongee plongee, Adherent adherent) throws TechnicalException, ResaException{
		//On inscrit pas qqlun en liste d'attente si il est dejà inscrit
		List<Adherent> inscrits = adherentDao.getAdherentsInscrits(plongee, null, "TOUS", null);
		for(Adherent inscrit : inscrits){
			if(inscrit.getNumeroLicense().equalsIgnoreCase(adherent.getNumeroLicense())){
				throw new ResaException("Inscription impossible enliste d'attente : Vous êtes déjà inscrit à la plongée.");
			}
		}
		return true;
	}

	/**
	 * Une plongée est ouverte s'il existe 
	 *  - au moins un DP et un pilote (ça peut-être la même personne)
	 *  reunion du 27/09/2010
	 *  - que le nombre de participants (inscrit + liste d'attente)
	 *  soit inferieure au nombre de plongeurs max
	 *  ceci pour bloquer l'inscription en cas de liste d'attente sur plongée pleine
	 */
	public boolean isOuverte(Plongee plongee){
		if(  plongee.isExistDP() && plongee.isExistPilote()){
			return true;
		} else{
			return false;
		}
	}

	public void fairePasserAttenteAInscrit(Plongee plongee, Adherent adherent)  throws TechnicalException{
		plongeeDao.moveAdherentAttenteToInscrit(plongee, adherent);
	}

	public synchronized void  inscrireAdherent(Plongee plongee, Adherent adherent, int typeMail) throws ResaException, TechnicalException {
		
		if(getNbPlaceRestante(plongee) > 0){
			//Appel DAO
			plongeeDao.inscrireAdherentPlongee(plongee, adherent);
			//Envoi mail
			if (typeMail == PlongeeMail.MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE){
				try {
					PlongeeMail pMail = new PlongeeMail( PlongeeMail.MAIL_INSCRIPTION_SUR_PLONGEE_FERMEE,
							plongee, adherent );
					pMail.sendMail("ADMIN");
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}else{
			throw new ResaException("Nombre Max de plongeurs atteint");
		}
	}

	public void inscrireAdherentEnListeAttente(Plongee plongee,	Adherent adherent, int typeMail)   throws ResaException, TechnicalException{
		//Appel DAO
		plongeeDao.inscrireAdherentAttente(plongee, adherent);
		//Envoi d'un mail
		if (typeMail == PlongeeMail.MAIL_PAS_ASSEZ_ENCADRANT){
			try {
				PlongeeMail pMail = new PlongeeMail(PlongeeMail.MAIL_PAS_ASSEZ_ENCADRANT,
						plongee, adherent);
				pMail.sendMail("ENCADRANT");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void deInscrireAdherent(Plongee plongee, Adherent adherent, int typeMail)   throws ResaException, TechnicalException{
		//Appel DAO
		plongeeDao.supprimeAdherentPlongee(plongee, adherent);
		
		//Envoi d'un mail si la desincription a lieu à moins de 24 heure de la plongée
		Date dateDuJour = new Date();
		Date datePlongee = plongee.getDate();
		if(ResaUtil.calculNbHeure(dateDuJour, datePlongee) < Parameters.getInt("desincription.alerte")){
			try {
				
				PlongeeMail pMail = new PlongeeMail(PlongeeMail.MAIL_DESINSCRIPTION_24,
						plongee, adherent);
				pMail.sendMail("ADMIN");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		
		//Envoi mail
		if (typeMail == PlongeeMail.MAIL_PLACES_LIBRES){
			try {
				
				PlongeeMail pMail = new PlongeeMail(PlongeeMail.MAIL_PLACES_LIBRES,
						plongee, adherent);
				pMail.sendMail("ADMIN");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		if (typeMail == PlongeeMail.MAIL_PLUS_ASSEZ_ENCADRANT){
			try {
				PlongeeMail pMail = new PlongeeMail(PlongeeMail.MAIL_PLUS_ASSEZ_ENCADRANT,
						plongee, adherent);
				pMail.sendMail("ADMIN");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void deInscrireAdherentEnListeAttente(Plongee plongee, Adherent adherent)  throws TechnicalException {
		plongeeDao.sortirAdherentAttente(plongee, adherent);
	}

	/**
	 * Met à jour l'indicateur de suppression
	 * indic = 1 vient l'admin
	 * indic = 2 vient l'adherent lui même
	 * indic = 3 vient du secretariat
	 */
	@Override
	public void supprimerDeLaListeDattente(Plongee plongee, Adherent adherent,	int indic) throws TechnicalException {
		plongeeDao.supprimerDeLaListeAttente(plongee, adherent, indic);
	}
	
}
