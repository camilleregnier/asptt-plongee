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

	@Override
	public void creerPlongee(Plongee plongee) {
		try {
			List<Plongee> plongees = rechercherPlongees(plongee.getDate(), plongee.getType());
			if(plongees.size() == 0){
				plongeeDao.create(plongee);
			}
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void modifierPlongee(Plongee plongee) {
		try {
			plongeeDao.update(plongee);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
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
			//l'adherent n'est pas encadrant : affiche que certaines plongées
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
			//l'adherent est encadrant : affiche toutes les plongées (même fermées)
			plongeesForAdherent.addAll(rechercherPlongeeTout());
		}
		return plongeesForAdherent;
	}

	public List<Plongee> rechercherPlongeesAdherentInscrit(Adherent adherent, int nbHours) {
		try {
			return plongeeDao.getPlongeesWhereAdherentIsInscrit(adherent, nbHours);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<Plongee> rechercherPlongeesOuvertesWithAttente(
			List<Plongee> plongees) {
		List<Plongee> plongeesAttente = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(plongee.isOuverte()){
				if( plongee.getParticipantsEnAttente().size() > 0 && plongee.getParticipants().size() < plongee.getNbMaxPlaces()){
					plongeesAttente.add(plongee);
				}
			}
		}
		return plongeesAttente;
	}
	
	@Override
	public List<Plongee> rechercherPlongees(Date date, String type) {
		try {
			return plongeeDao.getPlongeesWhithSameDate(date, type);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
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
					// ouvrir plongée
					isOk = 2;
				} else {
					// pas de assez de compétences pour ouvrir la plongée : pas inscrit !
					isOk = -1;
				}
				return isOk;
			}
			// verifier le nombre d'inscrit
			if(getNbPlaceRestante(plongee) < 0){
				// trop de monde : pas inscrit !
				isOk = -1;
				return isOk;
			}
			// Si DP = P5 => pas de BATM ou de P0
			String niveauDP = plongee.getDp().getNiveau();
			String encadrement = plongee.getDp().getEncadrement();
			if(niveauDP.equalsIgnoreCase("P5")){
				if(adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.BATM.toString()) 
					|| adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.P0.toString())){
					// inscription refusée
					isOk = -1;
					return isOk;
				}
			}
			// verifier le niveau mini
			int niveauAdherent = -1;
			if( ! adherent.getNiveau().equalsIgnoreCase(NiveauAutonomie.BATM.toString()) ){
				niveauAdherent = new Integer(adherent.getNiveau().substring(1)).intValue(); 
			}
			int niveauMinPlongee = new Integer(plongee.getNiveauMinimum().toString().substring(1)).intValue(); 
			if(niveauAdherent < niveauMinPlongee){
				// niveau mini requis : inscription refusée
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
			
			// Pour les plongeurs P0, P1
			if (niveauAdherent > 0 && niveauAdherent < 2){
				int res = (nbP0 + nbP1) / nbEncadrant;
				// max 4 P0 ou P1 par encadrant
				if (res > 4){
					// Pas assez d'encadrant : liste d'attente
					isOk = 0;
					return isOk;
				}
			}
			if (niveauAdherent < 0){
				int res = nbBATM / nbEncadrant;
				// max 1 bapteme par encadrant
				if (res > 1){
					// Pas assez d'encadrant : liste d'attente
					isOk = 0;
					return isOk;
				}
			}
			
			isOk = 1;
			return isOk;

			
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

	public void fairePasserAttenteAInscrit(Plongee plongee, Adherent adherent) {
		try {
			plongeeDao.moveAdherentAttenteToInscrit(plongee, adherent);
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
