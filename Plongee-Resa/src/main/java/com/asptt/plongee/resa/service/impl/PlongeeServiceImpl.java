package com.asptt.plongee.resa.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.util.tester.FormTester;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.service.PlongeeService;

public class PlongeeServiceImpl implements PlongeeService {

	private PlongeeDao plongeeDao;
	private AdherentDao adherentDao;
	
	public void setPlongeeDao(PlongeeDao plongeeDao) {
		this.plongeeDao = plongeeDao;
	}

	public void setAdherentDao(AdherentDao adherentDao) {
		this.adherentDao = adherentDao;
	}

	public Plongee rechercherPlongeeParId(Integer id) {
		try {
			return plongeeDao.findById(id);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public Plongee rechercherPlongeeParId(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Plongee> rechercherPlongeeTout() {
		try {
			return plongeeDao.findAll();
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<Plongee> rechercherPlongeeProchainJour() {
		try {
			List<Plongee> plongees = new ArrayList<Plongee>();
			int nbJour = 0;
			
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
			case 1: //Dimanche visible = j
				nbJour=1;
				break;
			case 2: //Lundi return pas d'inscription
				return plongees;
			case 3: //Mardi visible = j+2
				nbJour=3;
				break;
			case 4: //Mercredi visible = j+1
				nbJour=2;
				break;
			case 5: //Jeudi visible = j+4
				nbJour=5;
				break;
			case 6: //Vendredi visible = j+3
				nbJour=4;
				break;
			case 7: //Samedi visible = j+2
				nbJour=3;
				break;
			default:
				nbJour=0;
				break;
			}
			plongees.addAll(plongeeDao.getPlongeesForFewDay(nbJour));
			return plongees;
			
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new IllegalStateException(e);
		}
	}

	public List<Plongee> rechercherPlongeeAOuvrir(List<Plongee> plongees) {
		List<Plongee> plongeesFermees = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(!plongee.isOuverte()){
				plongeesFermees.add(plongee);
			}
		}
		return plongeesFermees;
	}

	public List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees) {
		List<Plongee> plongeesOuvertes = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(plongee.isOuverte()){
				plongeesOuvertes.add(plongee);
			}
		}
		return plongeesOuvertes;
	}

	/**
	 * Si l'adherent n'est pas déjà inscrit dans la liste
	 * de plongées passée en parametre
	 */
	public List<Plongee> rechercherPlongeePourInscriptionAdherent(Adherent adherent) {
		List<Plongee> plongeesForAdherent = new ArrayList<Plongee>();
		
		if(null == adherent.getEnumEncadrement()){
			List<Plongee> plongees = rechercherPlongeeProchainJour();
			for(Plongee plongee : plongees){
				if(plongee.isOuverte()){
					boolean isNotInscrit = true;
					for(Adherent adh : plongee.getParticipants()){
						if( adh.getNumeroLicense().equalsIgnoreCase(adherent.getNumeroLicense()) ){
							isNotInscrit=false;
							break;
						}
					}
					if(isNotInscrit){
						plongeesForAdherent.add(plongee);
					}
				}
			}
		} else {
			plongeesForAdherent.addAll(rechercherPlongeeTout());
		}
		return plongeesForAdherent;
	}

	public List<Plongee> rechercherPlongeesAdherentInscrit(Adherent adherent) {
		try {
			return plongeeDao.getPlongeesWhereAdherentIsInscrit(adherent);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<Plongee> rechercherPlongeesOuvertesWithAttente(
			List<Plongee> plongees) {
		List<Plongee> plongeesAttente = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(plongee.isOuverte()){
				if( plongee.getParticipantsEnAttente().size() > 0 ){
					plongeesAttente.add(plongee);
				}
			}
		}
		return plongeesAttente;
	}
	
	public List<Adherent> rechercherInscriptions(Plongee plongee,String niveauPlongeur, String niveauEncadrement) {
		try {
			return adherentDao.getAdherentsInscrits(plongee,null,null);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<Adherent> rechercherListeAttente(Plongee plongee) {
		try {
			return adherentDao.getAdherentsWaiting(plongee);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.asptt.plongee.resa.service.PlongeeService#isOuverte(com.asptt.plongee.resa.model.Plongee)
	public Boolean isOuverte(Plongee plongee) {
		if(plongee.isOuverte()){
			return true;
		} else {
			return false;
		}
	}
	 */

	public Integer getNbPlaceRestante(Plongee plongee) {
		try {
			return plongee.getNbMaxPlaces() - 
			adherentDao.getAdherentsInscrits(plongee,null,null).size();
		} catch (TechnicalException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	/**
	 * retourne
	 * 1 si ok
	 * 0 pour liste d'attente
	 * 2 ouvrir plongee
	 * -1 si ko
	 */
	public int isOkForResa(Plongee plongee, Adherent adherent) {
		// TODO : Si inscription d'un P2,P3,P4 => pas d'autres controles
		// TODO : Inscription P0, P1
		//		Si E2,E3,E4 => 4 x P0,P1 et/ou BATM
		//		Si BATM => 1 x E2,E3,E4

		int isOk = 1;
		try {
			// SI encadrant veux reserver une plongée pas encore ouverte:
			// si DP + Pilote > le brancher sur ouvrirplongee
			// sinon > impossible
			if( ! plongee.isOuverte()){
				if(adherent.isDp() && adherent.isPilote()){
					isOk = 2;
				} else {
					isOk = -1;
				}
				return isOk;
			}
			// verifier le nombre d'inscrit
			if(getNbPlaceRestante(plongee) < 0){
				isOk = -1;
				return isOk;
			}
			// Si DP = P5 ou E3 => pas de BATM ou de P0
			String niveauDP = plongee.getDp().getNiveau();
			String encadrement = plongee.getDp().getEncadrement();
			if(niveauDP.equalsIgnoreCase("P5") && encadrement.equalsIgnoreCase("E3")){
				if(adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.BATM.toString()) 
					|| adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.P0.toString())){
					isOk = -1;
					return isOk;
				}
			}
			// verifier le niveau mini
			int niveauAdherent = new Integer(adherent.getNiveau().substring(1)).intValue(); 
			int niveauMinPlongee = new Integer(plongee.getNiveauMinimum().toString().substring(1)).intValue(); 
			if(niveauAdherent < niveauMinPlongee){
				isOk = -1;
				return isOk;
			}
			List<Adherent> encadrants = adherentDao.getAdherentsInscrits(plongee, null, "TOUS");
			int nbEncadrant = encadrants.size();
			List<Adherent> plongeursP0 = adherentDao.getAdherentsInscrits(plongee, "P0", null);
			int nbP0 = plongeursP0.size();
			List<Adherent> plongeursP1 = adherentDao.getAdherentsInscrits(plongee, "P1", null);
			int nbP1 = plongeursP1.size();
			List<Adherent> plongeursBATM = adherentDao.getAdherentsInscrits(plongee, "BATM", null);
			int nbBATM = plongeursBATM.size();
			
		} catch (TechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isOk;
	}

	public boolean isOkForListeAttente(Plongee plongee, Adherent adherent) {
		// TODO Auto-generated method stub
		return true;
	}

	public void fairePasserAttenteInscrire(Plongee plongee, Adherent adherent) {
		try {
			plongeeDao.inscrireAdherentAttente(plongee, adherent);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public void inscrireAdherent(Plongee plongee, Adherent adherent) {
		try {
			plongeeDao.inscrireAdherentPlongee(plongee, adherent);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public void inscrireAdherentEnListeAttente(Plongee plongee,
			Adherent adherent) {
		try {
			plongeeDao.inscrireAdherentAttente(plongee, adherent);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public void deInscrireAdherent(Plongee plongee, Adherent adherent) {
		try {
			plongeeDao.supprimeAdherentPlongee(plongee, adherent);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public void deInscrireAdherentEnListeAttente(Plongee plongee,
			Adherent adherent) {
		try {
			plongeeDao.supprimeAdherentAttente(plongee, adherent);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
