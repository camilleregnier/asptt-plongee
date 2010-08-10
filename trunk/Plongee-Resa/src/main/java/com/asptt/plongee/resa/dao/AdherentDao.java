package com.asptt.plongee.resa.dao;

import java.util.List;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;


public interface AdherentDao extends GenericDao<Adherent, String> {
	
	public List<String> getStrRoles(Adherent adherent) throws TechnicalException;

	public int getIdRole(String libelle) throws TechnicalException;

	public List<Adherent> getAdherentsTous() throws TechnicalException;

	public List<Adherent> getAdherentsActifs() throws TechnicalException;

	public List<Adherent> getAdherentsInactifs() throws TechnicalException;

	public List<Adherent> getExternes() throws TechnicalException;

	public List<Adherent> getAdherentsInscrits(Plongee plongee, String niveauPlongeur, String niveauEncadrement) throws TechnicalException;

	public List<Adherent> getAdherentsWaiting(Plongee plongee) throws TechnicalException;
	
	public List<Adherent> getAdherentsLikeName(String name) throws TechnicalException;
}
