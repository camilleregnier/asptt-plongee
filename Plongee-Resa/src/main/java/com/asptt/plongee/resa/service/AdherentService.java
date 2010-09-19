package com.asptt.plongee.resa.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.asptt.plongee.resa.model.Adherent;

public interface AdherentService {

	public Adherent authentifierAdherent(String id, String pwd);

	public Adherent rechercherAdherentParIdentifiant(String id);
	
	public Adherent rechercherAdherentParIdentifiantTous(String id);
	
	public List<Adherent> rechercherPlongeurs();
	
	public List<Adherent> rechercherAdherentsTous();
	
	public List<Adherent> rechercherAdherentsActifs();
	
	public List<Adherent> rechercherAdherentsInactifs();
	
	public List<Adherent> rechercherExternes();

	public List<Adherent> rechercherAdherents(int first, int count);

	public List<Adherent> rechercherAdherentsRole(String roles);

	public List<Adherent> rechercherDPs(List<Adherent> adherents);
	
	public List<Adherent> rechercherPilotes(List<Adherent> adherents);

	public void creerAdherent(Adherent adherent);
	
	public void updateAdherent(Adherent adherent);

	public void updatePasswordAdherent(Adherent adherent);

	public void creerExterne(Adherent adherent);
	
}
