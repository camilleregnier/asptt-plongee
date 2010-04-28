package com.asptt.plongee.resa.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.asptt.plongee.resa.model.Adherent;

public interface AdherentService {

	public Adherent rechercherAdherentParIdentifiant(String id);
	
	public List<Adherent> rechercherAdherentTout();
	
	public Adherent rechercherDP(List<Adherent> adherents);
	
	public Adherent rechercherPilote(List<Adherent> adherents);

	public void creerAdherent(Adherent adherent);

}
