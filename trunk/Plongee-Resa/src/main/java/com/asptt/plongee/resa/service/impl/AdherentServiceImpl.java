package com.asptt.plongee.resa.service.impl;

import java.util.List;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.service.AdherentService;

public class AdherentServiceImpl implements AdherentService {

	private AdherentDao adherentDao;

	public void setAdherentDao(AdherentDao adherentDao) {
		this.adherentDao = adherentDao;
	}

	public Adherent rechercherAdherentParIdentifiant(String id) {
		try {
			return adherentDao.findById(id);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public List<Adherent> rechercherAdherentTout() {
		try {
			return adherentDao.findAll();
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public Adherent rechercherDP(List<Adherent> adherents) {
		try {
			return adherentDao.findDP(adherents);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

	public Adherent rechercherPilote(List<Adherent> adherents) {
		try {
			return adherentDao.findPilote(adherents);
		} catch (TechnicalException e) {
			throw new IllegalStateException(e);
		}
	}

}
