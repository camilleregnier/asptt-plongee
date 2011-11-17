package com.asptt.plongee.resa.service.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
				throw new ResaException("La date d'inscription doit \u00eatre ant\u00e9rieure \u00e0 la date de la plong\u00e9e");
			}
			plongeeDao.create(plongee);
		}
		else{
			throw new ResaException("Cette Plong\u00e9e existe d\u00e9j\u00e0");
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

	@Override
	public Plongee rechercherPlongeeParId(Integer id) throws TechnicalException{
		return plongeeDao.findById(id);
	}

	@Override
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

	private List<Plongee> rechercherPlongeePourAdherent()  throws TechnicalException{

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

	private List<Plongee> rechercherPlongeePourEncadrant()  throws TechnicalException{

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

	@Override
	public List<Plongee> rechercherPlongeeAOuvrir(List<Plongee> plongees) throws TechnicalException{
		
		List<Plongee> plongeesFermees = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(!isOuverte(plongee)){
				plongeesFermees.add(plongee);
			}
		}
		return plongeesFermees;
	}

	@Override
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
	@Override
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
				long nbJours = ResaUtil.checkDateCM(adherent.getDateCM(), plongee.getDate());
				if( nbJours > 0){
						plongeesForAdherent.add(plongee);
				}
			}	
		}

		return plongeesForAdherent;
	}

	@Override
	public List<Plongee> rechercherPlongeesAdherentInscrit(Adherent adherent, int nbHours)  throws TechnicalException{
		return plongeeDao.getPlongeesWhereAdherentIsInscrit(adherent, nbHours);
	}

	@Override
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

	@Override
	public List<Adherent> rechercherInscriptions(Plongee plongee,String niveauPlongeur, String niveauEncadrement, String trie)  throws TechnicalException{
		return adherentDao.getAdherentsInscrits(plongee,null,null,trie);
	}

	@Override
	public List<Adherent> rechercherListeAttente(Plongee plongee)  throws TechnicalException{
		return adherentDao.getAdherentsWaiting(plongee);
	}
	
	@Override
	public Integer getNbPlaceRestante(Plongee plongee)  throws TechnicalException{
		Integer nbPlace =  plongee.getNbMaxPlaces() - adherentDao.getAdherentsInscrits(plongee,null,null,null).size();
		return nbPlace;
	}

	@Override
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
	 */
	@Override
	public int isOkForResa(Plongee plongee, Adherent adherent) throws ResaException, TechnicalException {

		//initialisation du retour par defaut à 1 CàD : on inscrit
		int isOk = 1;
		
		//Init de l'heure courante
		Date dateDuJour = new Date();
		Calendar calDuJour = Calendar.getInstance();
		calDuJour.setTime(dateDuJour);
		int heureCourante = calDuJour.get(Calendar.HOUR_OF_DAY);
		
		//Verification de l'heure d'ouverture
		Calendar calOpen = Calendar.getInstance();
		calOpen.setTime(plongee.getDateVisible());
		int heureOuverture = getHeureOuverture(calOpen, adherent, plongee);
		
		long nbJours = ResaUtil.calculNbJour(dateDuJour, plongee.getDateVisible());

		//Test si l'adherent est déjà inscrit à la plongée
		for (Adherent inscrit : plongee.getParticipants()) {
			if (inscrit.getNumeroLicense().equalsIgnoreCase(adherent.getNumeroLicense())) {
				throw new ResaException("D\u00e9sol\u00e9 "+adherent.getPrenom()+" mais tu es d\u00e9j\u00e0 inscrit \u00e0 cette plong\u00e9e");
			}
		}
		
		//test actif == 1 ==> ok c un adherent
		// sinon c un externe donc inscription par secretariat
		if ( adherent.isVesteRouge() && adherent.getActifInt()==1 ){
			//C'est un encadrant un Dp ou un pilote
			//L'inscription est ouverte à tous moments
		} else {
			if( nbJours > 0){
				//La date de visibilité de la plongée n'est pas atteinte : on attend
				throw new ResaException("D\u00e9sol\u00e9 "+adherent.getPrenom()+
						"il faudra attendre : le "+ResaUtil.getDateString(plongee.getDateVisible())+" \u00e0 "+heureOuverture+" heures pour t'inscrire.");
			} else {
				if( nbJours == 0){
					//C'est le jour de visibilité de la plongée : On regarde l'heure avant de donner accès à l'inscription
					if( heureCourante < heureOuverture){
						// Trop tot : attendre l'heure d'ouverture
						throw new ResaException("D\u00e9sol\u00e9 "+adherent.getPrenom()+" mais l'inscription ouvre \u00e0 partir de : "+heureOuverture+" heures.");
					}
				}
			}
			//Si le nombre de jour est < 0 : la date de visi est depassée donc on inscrit
		}
		
		// verifier le nombre d'inscrit
		if(getNbPlaceRestante(plongee) <= 0){
			// trop de monde : inscription en liste d'attente avec msg 'bateau plein'
			isOk = 0;
			return isOk;
		}

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
				throw new ResaException("Inscription impossible sur cette plong\u00e9e : Cette plong\u00e9e n'est pas ouverte");
			}
			return isOk;
		}
		
		// Si DP = P5 et pas encadrant (plus que E2) => pas de BATM ou de P0
		String niveauDP = (plongee.getDp().getEncadrement() != null && !plongee.getDp().getEncadrement().equals(Encadrement.E2)) ? plongee.getDp().getEncadrement() : plongee.getDp().getNiveau();
		if(niveauDP.equalsIgnoreCase("P5")){
			if(adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.BATM.toString()) 
				|| adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.P0.toString())){
				// inscription refusée
				throw new ResaException("Inscription impossible sur cette plong\u00e9e : Les BATM ou P0 ne sont pas admis avec un DP P5");
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
			throw new ResaException("Inscription impossible sur cette plong\u00e9e : Niveau insuffisant");
		}

		//On inscrit pas qqlun si il est dejà en liste d'attente
		List<Adherent> enAttente = adherentDao.getAdherentsWaiting(plongee);
		for(Adherent attente : enAttente){
			if(attente.getNumeroLicense().equalsIgnoreCase(adherent.getNumeroLicense())){
				throw new ResaException("Inscription impossible sur cette plong\u00e9e : Vous etes d\u00e9j\u00e0 en liste d'attente.");
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
		
		//Issue 69 : limiter le nombre d'encadrant à 7
		int nbEncadrantMax = new Integer(Parameters.getString("encadrant.max")).intValue();
		if (nbEncadrant >= nbEncadrantMax && adherent.isVesteRouge()){
			//On créé un externe bidon pour passer a le methode de recherche de l'heure d'ouvertutre
			//afin de ramener l'heure d'ouverture pour les externes
			Adherent ext = new Adherent();
			ext.setActifInt(2);
			int heureOuvertureExterne = getHeureOuverture(calOpen, ext, plongee);
			if( nbJours > 0){
				//La date de visibilité de la plongée n'est pas atteinte : on attend
				throw new ResaException("D\u00e9sol\u00e9 "+adherent.getPrenom()+" mais " +
				"le nombre limite d'encadrant est atteint, " +
				"il faudra attendre : le "+ResaUtil.getDateString(plongee.getDateVisible())+" \u00e0 "+heureOuvertureExterne+" heures pour t'inscrire.");
			} else {
				if( nbJours == 0){
					//C'est le jour de visibilité de la plongée : On regarde l'heure avant de donner accès à l'inscription
					if( heureCourante < heureOuvertureExterne){
						// Trop tot : attendre l'heure d'ouverture
						throw new ResaException("D\u00e9sol\u00e9 "+adherent.getPrenom()+" mais l'inscription ouvre \u00e0 partir de : "+heureOuvertureExterne+" heures.");
					}
				}
			}
			//Si le nombre de jour est < 0 : la date de visi est depassée donc on inscrit
		}
		
		// SI on est arrivé jusqu'ici : c'est bon => on inscrit
		isOk = 1;
		return isOk;

	}

	@Override
	public boolean isOkForListeAttente(Plongee plongee, Adherent adherent) throws TechnicalException, ResaException{

		//On inscrit pas qqlun en liste d'attente si il est dejà inscrit
		List<Adherent> inscrits = adherentDao.getAdherentsInscrits(plongee, null, "TOUS", null);
		for(Adherent inscrit : inscrits){
			if(inscrit.getNumeroLicense().equalsIgnoreCase(adherent.getNumeroLicense())){
				throw new ResaException("Inscription impossible enliste d'attente : Vous \u00eates d\u00e9j\u00e0 inscrit \u00e0 la plong\u00e9e.");
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
	@Override
	public boolean isOuverte(Plongee plongee){
		if(  plongee.isExistDP() && plongee.isExistPilote()){
			return true;
		} else{
			return false;
		}
	}

	@Override
	public void fairePasserAttenteAInscrit(Plongee plongee, Adherent adherent)  throws TechnicalException{
		plongeeDao.moveAdherentAttenteToInscrit(plongee, adherent);
	}

	@Override
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

	@Override
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

	@Override
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
				pMail.sendMail("ADMIN",adherent);
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

	@Override
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
	
	/**
	 * Verifie si le certificat medical de l'adherent est encore valable
	 * @param Adherent adherent
	 * @param Plongee plongee
	 * @return ResaException si Ko
	 * si nbMois =-1 => perimé
	 * si nbMois = 0 => Warning : dans le derniers mois 
	 * si nbMois = 1 => Warning : mois-1 avec le nombre de jour
	 * si nbMois = 2 => No soucie 
	 *  
	 */
	@Override
	public void checkCertificatMedical(Adherent adherent, Plongee plongee) throws TechnicalException, ResaException{
		
		Date dateCompare = new Date();
		if (null != plongee){
			dateCompare = plongee.getDate();
		}
		long nbJours  = ResaUtil.checkDateCM(adherent.getDateCM(), dateCompare);
		String libCM ="";
		if(nbJours <= 0){
			//CM Perimé
			throw new ResaException("\n ton certificat m\u00e9dical est p\u00e9rim\u00e9");
		} else {
			//On leve l'exception sil reste moins de 31 jours 
			if(nbJours <= 31){
				throw new ResaException("ATTENTION plus que "+nbJours+" jours avant que ton certificat m\u00e9dical soit p\u00e9rim\u00e9");
			}
		}
	}

	@Override
	public int getHeureOuverture(Calendar cal, Adherent adh, Plongee plongee){

		int numJour = cal.get(Calendar.DAY_OF_WEEK);			
		int heure;
		//Calendar de la plongée pour rechercher le numero du jour de la plongée
		Calendar calPlongee = Calendar.getInstance();
		calPlongee.setTime(plongee.getDate());
		int numPlongee = calPlongee.get(Calendar.DAY_OF_WEEK);
		switch (numJour) {
			case 1: //Dimanche
				//test actif == 1 ==> ok c un adherent
				// sinon c un externe donc inscription par secretatiat
				if(adh.getActifInt()==1){
					heure=Parameters.getInt("ouverture.dimanche.adh");
				} else {
					heure=Parameters.getInt("ouverture.dimanche.sct");
				}
				break;
			case 2: //Lundi
				if(adh.getActifInt()== 1){
					heure=Parameters.getInt("ouverture.lundi.adh");
				} else {
					heure=Parameters.getInt("ouverture.lundi.sct");
				}
				break;
			case 3: //Mardi
				if(adh.getActifInt()== 1){
					heure=Parameters.getInt("ouverture.mardi.adh");
				} else {
					heure=Parameters.getInt("ouverture.mardi.sct");
				}
				break;
			case 4: //Mercredi 
				if(adh.getActifInt()== 1){
				heure=Parameters.getInt("ouverture.mercredi.adh");
				} else {
					heure=Parameters.getInt("ouverture.mercredi.sct");
				}
				break;
			case 5: //Jeudi
				if(adh.getActifInt()== 1){
				heure=Parameters.getInt("ouverture.jeudi.adh");
				} else {
					heure=Parameters.getInt("ouverture.jeudi.sct");
				}
				break;
			case 6: //Vendredi
				if(adh.getActifInt()== 1){
				heure=Parameters.getInt("ouverture.vendredi.adh");
				} else {
					heure=Parameters.getInt("ouverture.vendredi.sct");
				}
				break;
			case 7: //Samedi
				if(adh.getActifInt()== 1){
					heure=Parameters.getInt("ouverture.samedi.adh");
				} else {
					heure=Parameters.getInt("ouverture.samedi.sct");
				}
				break;
			default:
				heure=0;
				break;
		}
		return heure;
	}

}
