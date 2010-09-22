package com.asptt.plongee.resa.service;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeService {

	public Plongee rechercherPlongeeParId(Integer id);
	public List<Plongee> rechercherPlongeeTout();
	public List<Plongee> rechercherPlongeeProchainJour(Adherent adherent);
	public List<Plongee> rechercherPlongeeAOuvrir(List<Plongee> plongees);
	public List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees);
	public List<Plongee> rechercherPlongeePourInscriptionAdherent(Adherent adherent);
	public List<Plongee> rechercherPlongeesAdherentInscrit(Adherent adherent, int nbHours);
	public List<Plongee> rechercherPlongeesOuvertesWithAttente(List<Plongee> plongees);
	public List<Plongee> rechercherPlongees(Date date, String type);
	public List<Adherent> rechercherInscriptions(Plongee plongee, String niveauPlongeur, String niveauEncadrement);
	public List<Adherent> rechercherListeAttente(Plongee plongee);
	
	public void creerPlongee(Plongee plongee) throws ResaException, TechnicalException;
	public void supprimerPlongee(Plongee plongee) throws TechnicalException;
	public void modifierPlongee(Plongee plongee);

	public Integer getNbPlaceRestante(Plongee plongee);
	
	public int isOkForResa(Plongee plongee, Adherent adherent) throws ResaException;
	public boolean isOkForListeAttente(Plongee plongee, Adherent adherent) throws TechnicalException, ResaException;
	public boolean isEnoughEncadrant(Plongee plongee) throws TechnicalException;
	
	public void inscrireAdherent(Plongee plongee, Adherent adherent) throws ResaException;
	public void inscrireAdherentEnListeAttente(Plongee plongee, Adherent adherent);
	public void fairePasserAttenteAInscrit(Plongee plongee, Adherent adherent);

	public void deInscrireAdherent(Plongee plongee, Adherent adherent);
	public void deInscrireAdherentEnListeAttente(Plongee plongee, Adherent adherent);
}
