package com.asptt.plongee.resa.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.mail.MessagingException;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.mail.PlongeeMail;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.AdherentComparatorNom;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.ResaConstants;
import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.util.ResaUtil;

public class AdherentServiceImpl implements AdherentService, Serializable {

	private static final long serialVersionUID = -8552502001819883624L;
	private AdherentDao adherentDao;

	public void setAdherentDao(AdherentDao adherentDao) {
		this.adherentDao = adherentDao;
	}

	public Adherent authentifierAdherent(String id, String pwd) throws TechnicalException{
		return adherentDao.authenticateAdherent(id, pwd);
	}
	
	public Adherent rechercherAdherentParIdentifiant(String id) throws TechnicalException{
			return adherentDao.findById(id);
	}
	
	public Adherent rechercherAdherentParIdentifiantTous(String id) throws TechnicalException{
			return adherentDao.findByIdAll(id);
	}

	public List<Adherent> rechercherPlongeurs() throws TechnicalException{
			return adherentDao.findAll();
	}

	public List<Adherent> rechercherAdherentsTous() throws TechnicalException{
			return adherentDao.getAdherentsTous();
	}

	public List<Adherent> rechercherAdherentsActifs() throws TechnicalException{
			return adherentDao.getAdherentsActifs();
	}

	public List<Adherent> rechercherAdherentsInactifs() throws TechnicalException{
			return adherentDao.getAdherentsInactifs();
	}

	public List<Adherent> rechercherExternes() throws TechnicalException{
			return adherentDao.getExternes();
	}

	/**
	 * Cette methode est appelée uniquement par l'AdherentDataProvider
	 * dans le seul cas ou il faut faire une recherche : normalement jamais!
	 */
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
	public List<Adherent> rechercherDPsNonInscrits(List<Adherent> adherents, Plongee plongee) throws TechnicalException{
		List<Adherent> dps = new ArrayList<Adherent>();
		HashMap<String,Adherent> dpInscrits = new HashMap<String,Adherent>();
		
		for(Adherent a : plongee.getParticipants()){
			if(a.isDp()){
				dpInscrits.put(a.getNumeroLicense(),a);
			}
		}
		
		for(Adherent a : adherents){
			if(a.isDp() && ! dpInscrits.containsKey(a.getNumeroLicense())){
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
	public List<Adherent> rechercherPilotesNonInscrits(List<Adherent> adherents, Plongee plongee) throws TechnicalException{
		List<Adherent> pilotes = new ArrayList<Adherent>();
		HashMap<String,Adherent> pilotesInscrits = new HashMap<String,Adherent>();
		
		for(Adherent a : plongee.getParticipants()){
			if(a.isPilote()){
				pilotesInscrits.put(a.getNumeroLicense(),a);
			}
		}
		
		for(Adherent a : adherents){
			if(a.isPilote() && ! pilotesInscrits.containsKey(a.getNumeroLicense())){
				pilotes.add(a);
			}
		}
		return pilotes;
	}

	public List<Adherent> rechercherAdherentsRole(String role) throws TechnicalException{
			return adherentDao.getAdherentsLikeRole(role);
	}
	
	public void creerAdherent(Adherent adherent) throws TechnicalException{
			adherentDao.create(adherent);
	}
	
	public void updateAdherent(Adherent adherent, int typeMail) throws TechnicalException, ResaException{
		
		adherentDao.update(adherent);	
		if (typeMail == PlongeeMail.MAIL_MODIF_INFO_ADHERENT){
			PlongeeMail pMail;
			try {
				pMail = new PlongeeMail(PlongeeMail.MAIL_PAS_ASSEZ_ENCADRANT,
						null, adherent);
				pMail.sendMail("ADMIN");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updatePasswordAdherent(Adherent adherent) throws TechnicalException{
			adherentDao.updatePassword(adherent);
	}
	
	public void creerExterne(Adherent adherent) throws TechnicalException{
			Integer numExt = adherentDao.getExternes().size()+1;
			adherent.setNumeroLicense(ResaConstants.LICENSE_EXTERNE.concat(numExt.toString()));
			adherent.setActifInt(2);
			adherent.setPilote(false);
			adherent.setDp(false);
			adherentDao.create(adherent);
	}

	public List<Message> rechercherMessage() throws TechnicalException{
		return adherentDao.getMessage();
	}

	public Message updateMessage(Message message) throws TechnicalException{
		return adherentDao.updateMessage(message);
	}
	
	public Message createMessage(Message message) throws TechnicalException{
		return adherentDao.createMessage(message);
	}

}
