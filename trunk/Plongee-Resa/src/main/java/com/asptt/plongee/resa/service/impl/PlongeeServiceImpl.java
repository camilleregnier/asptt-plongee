package com.asptt.plongee.resa.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
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
//			return plongeeDao.findAll();
			return plongeeDao.getPlongeesForWeek();
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<Plongee> rechercherPlongeeAOuvrir(List<Plongee> plongees) {
		List<Plongee> plongeesFermees = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(!isOuverte(plongee)){
				plongeesFermees.add(plongee);
			}
		}
		return plongeesFermees;
	}

	public List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees) {
		List<Plongee> plongeesOuvertes = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(isOuverte(plongee)){
				plongeesOuvertes.add(plongee);
			}
		}
		return plongeesOuvertes;
	}

	public List<Plongee> rechercherPlongeeOuverteForAdherent(
			List<Plongee> plongees, Adherent adherent) {
		List<Plongee> plongeesOuvertes = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(isOuverte(plongee)){
				boolean isNotInscrit = true;
				for(Adherent adh : plongee.getParticipants()){
					if( adh.getNumeroLicense().equalsIgnoreCase(adherent.getNumeroLicense()) ){
						isNotInscrit=false;
						break;
					}
				}
				if(isNotInscrit){
					plongeesOuvertes.add(plongee);
				}
			}
		}
		return plongeesOuvertes;
	}

	public List<Plongee> rechercherPlongeesOuvertesWithAttente(
			List<Plongee> plongees) {
		List<Plongee> plongeesAttente = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(isOuverte(plongee)){
				if( plongee.getParticipantsEnAttente().size() > 0 ){
					plongeesAttente.add(plongee);
				}
			}
		}
		return plongeesAttente;
	}

	public List<Plongee> rechercherPlongeeInscritForAdherent(Adherent adherent) {
		try {
			return plongeeDao.getPlongeesForAdherent(adherent);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<Adherent> rechercherInscriptions(Plongee plongee) {
		try {
			return adherentDao.getAdherentsInscrits(plongee);
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
	 */
	public Boolean isOuverte(Plongee plongee) {
		if(null != plongee.getDp() && null != plongee.getPilote()){
			return true;
		} else {
			return false;
		}
	}

	public Integer getNbPlaceRestante(Plongee plongee) {
		try {
			return plongee.getNbMaxPlaces() - 
			adherentDao.getAdherentsWaiting(plongee).size();
		} catch (TechnicalException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public boolean isOkForResa(Plongee plongee, Adherent adherent) {
		// TODO Auto-generated method stub
		return false;
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
