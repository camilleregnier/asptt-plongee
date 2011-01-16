package com.asptt.plongee.resa.service;

import java.util.List;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.Plongee;

public interface AdherentService {
	
	List<Message> rechercherMessage();
	
	Adherent authentifierAdherent(String id, String pwd);

	Adherent rechercherAdherentParIdentifiant(String id);
	
	Adherent rechercherAdherentParIdentifiantTous(String id);
	
	List<Adherent> rechercherPlongeurs();
	
	List<Adherent> rechercherAdherentsTous();
	
	List<Adherent> rechercherAdherentsActifs();
	
	List<Adherent> rechercherAdherentsInactifs();
	
	List<Adherent> rechercherExternes();

	List<Adherent> rechercherAdherents(int first, int count);

	List<Adherent> rechercherAdherentsRole(String roles);

	List<Adherent> rechercherDPs(List<Adherent> adherents);
	
	List<Adherent> rechercherDPsNonInscrits(List<Adherent> adherents, Plongee plongee);
	
	List<Adherent> rechercherPilotes(List<Adherent> adherents);
	
	List<Adherent> rechercherPilotesNonInscrits(List<Adherent> adherents, Plongee plongee);
	
	void creerAdherent(Adherent adherent);
	
	void updateAdherent(Adherent adherent);
	
	void updatePasswordAdherent(Adherent adherent);

	void creerExterne(Adherent adherent);

	Message updateMessage(Message message);

	Message createMessage(Message message);

	
}
