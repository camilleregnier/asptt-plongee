package com.asptt.plongee.resa.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentComparatorNom;
import com.asptt.plongee.resa.model.ResaConstants;
import com.asptt.plongee.resa.service.AdherentService;

public class AdherentServiceImpl implements AdherentService {

	private AdherentDao adherentDao;

	public void setAdherentDao(AdherentDao adherentDao) {
		this.adherentDao = adherentDao;
	}

	public Adherent rechercherAdherentParIdentifiant(String id) throws TechnicalException{
//		try {
			return adherentDao.findById(id);
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}
	
	public Adherent rechercherAdherentParIdentifiantTous(String id) throws TechnicalException{
//		try {
			return adherentDao.findByIdAll(id);
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}

	public List<Adherent> rechercherPlongeurs() throws TechnicalException{
//		try {
			return adherentDao.findAll();
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}

	public List<Adherent> rechercherAdherentsTous() throws TechnicalException{
//		try {
			return adherentDao.getAdherentsTous();
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}

	public List<Adherent> rechercherAdherentsActifs() throws TechnicalException{
//		try {
			return adherentDao.getAdherentsActifs();
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}

	public List<Adherent> rechercherAdherentsInactifs() throws TechnicalException{
//		try {
			return adherentDao.getAdherentsInactifs();
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}

	public List<Adherent> rechercherExternes() throws TechnicalException{
//		try {
			return adherentDao.getExternes();
//		} catch (TechnicalException e) {
//			throw new IllegalStateException(e);
//		}
	}

	/**
	 * Cette methode est appelée uniquement par l'AdherentDataProvider
	 * dans le seul cas ou il faut faire une recherche : normalement jamais!
	 */
	@Override
	public List<Adherent> rechercherAdherents(int first, int count) throws TechnicalException{
		TreeSet tAdh = new TreeSet(new AdherentComparatorNom());
		tAdh.addAll(rechercherAdherentsTous());
		
		List<Adherent> adhTrie = new ArrayList<Adherent>();
		adhTrie.addAll(tAdh);
		List<Adherent> sousAdhTrie = new ArrayList<Adherent>();
		for (int i = 0 ; i < count ; i++){
			if (first+i < adhTrie.size()){
				sousAdhTrie.add(adhTrie.get(first+i));
			}
		}
		return sousAdhTrie;
	}

	public List<Adherent> rechercherDPs(List<Adherent> adherents) throws TechnicalException{
		List<Adherent> dps = new ArrayList<Adherent>();
		for(Adherent a : adherents){
			if(a.isDp()){
				dps.add(a);
			}
		}
		return dps;
	}

	public List<Adherent> rechercherPilotes(List<Adherent> adherents) throws TechnicalException{
		List<Adherent> pilotes = new ArrayList<Adherent>();
		for(Adherent a : adherents){
			if(a.isPilote()){
				pilotes.add(a);
			}
		}
		return pilotes;
	}

	public List<Adherent> rechercherAdherentsRole(String role) throws TechnicalException{
//		try {
			return adherentDao.getAdherentsLikeRole(role);
//		} catch (TechnicalException e) {
//			e.printStackTrace();
//			throw new IllegalStateException(e);
//		}
		
	}
	
	public void creerAdherent(Adherent adherent) throws TechnicalException{
//		try {
			adherentDao.create(adherent);
//		} catch (TechnicalException e) {
//			e.printStackTrace();
//			throw new IllegalStateException(e);
//		}
		
	}
	
	public void updateAdherent(Adherent adherent) throws TechnicalException{
//		try {
			adherentDao.update(adherent);
//		} catch (TechnicalException e) {
//			e.printStackTrace();
//			throw new IllegalStateException(e);
//		}
		
	}
	
	public void creerExterne(Adherent adherent) throws TechnicalException{
//		try {
			Integer numExt = adherentDao.getExternes().size()+1;
			adherent.setNumeroLicense(ResaConstants.LICENSE_EXTERNE.concat(numExt.toString()));
			adherent.setActifInt(2);
			adherent.setPilote(false);
			adherent.setDp(false);
			adherentDao.create(adherent);
//		} catch (TechnicalException e) {
//			e.printStackTrace();
//			throw new IllegalStateException(e);
//		}
		
	}
	
}
