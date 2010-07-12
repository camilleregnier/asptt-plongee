package com.asptt.plongee.resa.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentComparatorNom;
import com.asptt.plongee.resa.service.AdherentService;

public class AdherentServiceImpl implements AdherentService {

	public void creerAdherent(Adherent adherent) {
		try {
			adherentDao.create(adherent);
		} catch (TechnicalException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		
	}
	
	public void updateAdherent(Adherent adherent) {
		try {
			adherentDao.update(adherent);
		} catch (TechnicalException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		
	}
	
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

	@Override
	public List<Adherent> rechercherAdherent(int first, int count) {
		TreeSet tAdh = new TreeSet(new AdherentComparatorNom());
		tAdh.addAll(rechercherAdherentTout());
		
		List<Adherent> adhTrie = new ArrayList<Adherent>();
		adhTrie.addAll(tAdh);
		List<Adherent> sousAdhTrie = new ArrayList<Adherent>();
		for (int i = 0 ; i <= count ; i++){
			if (first+i < adhTrie.size()){
				sousAdhTrie.add(adhTrie.get(first+i));
			}
		}
		
		// TODO Auto-generated method stub
		return sousAdhTrie;
	}

	public List<Adherent> rechercherDPs(List<Adherent> adherents) {
		List<Adherent> dps = new ArrayList<Adherent>();
		for(Adherent a : adherents){
			if(a.isDp()){
				dps.add(a);
			}
		}
		return dps;
	}

	public List<Adherent> rechercherPilotes(List<Adherent> adherents) {
		List<Adherent> pilotes = new ArrayList<Adherent>();
		for(Adherent a : adherents){
			if(a.isPilote()){
				pilotes.add(a);
			}
		}
		return pilotes;
//		try {
//			return adherentDao.findPilote(adherents);
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}

}
