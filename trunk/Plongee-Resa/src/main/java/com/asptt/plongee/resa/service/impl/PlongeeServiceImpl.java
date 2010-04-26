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
			return plongeeDao.findAll();
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	
	
	public List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees) {
		List<Plongee> plongeesOuvertes = new ArrayList<Plongee>();
		for(Plongee plongee : plongees){
			if(isOuverte(plongee)){
				plongeesOuvertes.add(plongee);
			}
		}
		return plongeesOuvertes;
//		try {
//			return plongeeDao.findAllOuvertes();
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
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

}
