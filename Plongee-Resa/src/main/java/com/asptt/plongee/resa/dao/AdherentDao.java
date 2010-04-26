package com.asptt.plongee.resa.dao;

import java.util.List;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;


public interface AdherentDao extends GenericDao<Adherent, String> {
	
	public List<String> getRoles(Adherent adherent) throws TechnicalException;

	public List<Adherent> getAdherentsInscrits(Plongee plongee) throws TechnicalException;

	public List<Adherent> getAdherentsWaiting(Plongee plongee) throws TechnicalException;
	
	public Adherent findDP(List<Adherent> adhrents) throws TechnicalException;

	public Adherent findPilote(List<Adherent> adhrents) throws TechnicalException;

}
