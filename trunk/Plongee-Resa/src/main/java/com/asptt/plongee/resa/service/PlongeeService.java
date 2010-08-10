package com.asptt.plongee.resa.service;

import java.util.List;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeService {

	public Plongee rechercherPlongeeParId(String id);
	public Plongee rechercherPlongeeParId(Integer id);
	public List<Plongee> rechercherPlongeeTout();
	public List<Plongee> rechercherPlongeeProchainJour();
	public List<Plongee> rechercherPlongeeAOuvrir(List<Plongee> plongees);
	public List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees);
	public List<Plongee> rechercherPlongeePourInscriptionAdherent(Adherent adherent);
	public List<Plongee> rechercherPlongeesAdherentInscrit(Adherent adherent);
	public List<Plongee> rechercherPlongeesOuvertesWithAttente(List<Plongee> plongees);
	public List<Adherent> rechercherInscriptions(Plongee plongee, String niveauPlongeur, String niveauEncadrement);
	public List<Adherent> rechercherListeAttente(Plongee plongee);
	
	public void creerPlongee(Plongee plongee);
	public void modifierPlongee(Plongee plongee);

	public Integer getNbPlaceRestante(Plongee plongee);
	
	public int isOkForResa(Plongee plongee, Adherent adherent);
	public boolean isOkForListeAttente(Plongee plongee, Adherent adherent);
	
	public void inscrireAdherent(Plongee plongee, Adherent adherent);
	public void inscrireAdherentEnListeAttente(Plongee plongee, Adherent adherent);
	public void fairePasserAttenteInscrire(Plongee plongee, Adherent adherent);

	public void deInscrireAdherent(Plongee plongee, Adherent adherent);
	public void deInscrireAdherentEnListeAttente(Plongee plongee, Adherent adherent);
}
