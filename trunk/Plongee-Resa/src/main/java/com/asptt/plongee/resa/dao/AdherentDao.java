package com.asptt.plongee.resa.dao;

import java.util.List;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;


public interface AdherentDao extends GenericDao<Adherent, String> {
	
	List<String> getStrRoles(Adherent adherent) throws TechnicalException;

	int getIdRole(String libelle) throws TechnicalException;

	List<Adherent> getAdherentsTous() throws TechnicalException;

	List<Adherent> getAdherentsActifs() throws TechnicalException;

	List<Adherent> getAdherentsInactifs() throws TechnicalException;

	List<Adherent> getExternes() throws TechnicalException;

	List<Adherent> getAdherentsInscrits(Plongee plongee, String niveauPlongeur, String niveauEncadrement, String trie) throws TechnicalException;

	List<Adherent> getAdherentsWaiting(Plongee plongee) throws TechnicalException;
	
	List<Adherent> getAdherentsLikeName(String name) throws TechnicalException;
	
	List<Adherent> getAdherentsLikeRole(String role) throws TechnicalException;
	
	Adherent findByIdAll(String id) throws TechnicalException;

	Adherent updatePassword(Adherent adherent) throws TechnicalException;

	Adherent authenticateAdherent(String id, String pwd) throws TechnicalException;
}
