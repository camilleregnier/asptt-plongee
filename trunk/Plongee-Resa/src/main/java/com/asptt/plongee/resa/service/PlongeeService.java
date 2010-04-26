package com.asptt.plongee.resa.service;

import java.util.List;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeService {

	public Plongee rechercherPlongeeParId(String id);
	public List<Plongee> rechercherPlongeeTout();
	public List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees);
	public List<Adherent> rechercherInscriptions(Plongee plongee);
	public List<Adherent> rechercherListeAttente(Plongee plongee);
	public Boolean isOuverte(Plongee plongee);
}
