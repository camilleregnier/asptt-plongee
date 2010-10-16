package com.asptt.plongee.resa.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

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

public class PlongeeServiceImpl implements PlongeeService {

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
		
		//nombre de jour pour la visiblité des plongées
		int nbJour = 0;
		
		//jour 'a partir' de la date courante visible 
		// 0 = le jour même, 1 = le lendemain,  etc
		// initialisé pour le jour même (cas de l'adherent l'ambda)
		int aPartir = 0;
		
//			GregorianCalendar gc = new GregorianCalendar();
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//			Date maDate = sdf.parse("21/07/2010");
//			gc.setTime(maDate);
//			String maDateAffichee = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE);			
//			int monNumJour = gc.get(Calendar.DAY_OF_WEEK);			
		
		Date dateDuJour = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateDuJour);
		int numJour = cal.get(Calendar.DAY_OF_WEEK);			

		switch (numJour) {
		case 1: //Dimanche visible j+3 = lundi mardi
			nbJour = Parameters.getInt("visible.dimanche");
			break;
		case 2: //Lundi visible j+2 = mardi
			nbJour = Parameters.getInt("visible.lundi");
			break;
		case 3: //Mardi visible = j+3 : mercredi, jeudi
			nbJour = Parameters.getInt("visible.mardi");
			break;
		case 4: //Mercredi visible = j+2 : jeudi 
			nbJour = Parameters.getInt("visible.mercredi");
			break;
		case 5: //Jeudi visible = j+6 : vendredi, samedi, dimanche, lundi , mardi
			nbJour = Parameters.getInt("visible.jeudi");;
			break;
		case 6: //Vendredi visible = j+5 : samedi, dimanche, lundi , mardi
			nbJour = Parameters.getInt("visible.vendredi");;
			break;
		case 7: //Samedi visible = j+4 : dimanche, lundi , mardi
			nbJour = Parameters.getInt("visible.samedi");;
			break;
		default:
			//on voit le lendemain
			nbJour=2;
			break;
		}
		
		// reunion du 27/09/2010
		// Les encadrant peuvent visualiser les 15 jours suivants à partir du jour même 
//		if (adherent.getEncadrement() != null || adherent.isDp() || adherent.isPilote()) {
		if (adherent.isVesteRouge()) {
			nbJour = Parameters.getInt("visible.max");
			aPartir = 0;
		}
		//Appel au service DAO
		List<Plongee> plongeeTrouvees = plongeeDao.getPlongeesForFewDay(aPartir, nbJour);
		for(Plongee plongee: plongeeTrouvees){
			if(adherent.isVesteRouge() ){
				plongees.add(plongee);
			} else {
				if(isOuverte(plongee)){
					plongees.add(plongee);
				}
			}
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
		List<Plongee> plongees = rechercherPlongeeProchainJour(adherent);
		
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
		//Pour ne pas afficher de nombre negatif
//		if (nbPlace < 0){
//			nbPlace = 0;
//		}
		return nbPlace;
//		return 0;
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
		plongeeDao.supprimeAdherentAttente(plongee, adherent);
	}
	
}
