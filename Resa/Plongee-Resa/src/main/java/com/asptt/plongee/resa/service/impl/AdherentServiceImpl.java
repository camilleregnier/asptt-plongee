package com.asptt.plongee.resa.service.impl;

import java.util.List;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.service.AdherentService;

public class AdherentServiceImpl implements AdherentService {

	private AdherentDao adherentDao;
	
	
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

	public void setAdherentDao(AdherentDao adherentDao) {
		this.adherentDao = adherentDao;
	}
}
