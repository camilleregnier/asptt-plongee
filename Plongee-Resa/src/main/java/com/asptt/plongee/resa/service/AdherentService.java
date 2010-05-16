package com.asptt.plongee.resa.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.asptt.plongee.resa.model.Adherent;

public interface AdherentService {

	public Adherent rechercherAdherentParIdentifiant(String id);
	
	public List<Adherent> rechercherAdherentTout();
	
	public List<Adherent> rechercherDPs(List<Adherent> adherents);
	
	public List<Adherent> rechercherPilotes(List<Adherent> adherents);

	public void creerAdherent(Adherent adherent);

}
