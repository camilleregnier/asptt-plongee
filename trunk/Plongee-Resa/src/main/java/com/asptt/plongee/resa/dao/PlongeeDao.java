package com.asptt.plongee.resa.dao;


import java.util.List;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeDao extends GenericDao<Plongee, Integer>{

	public List<Plongee> getPlongeesForWeek() throws TechnicalException;
	
	public List<Plongee> getPlongeesForAdherent(Adherent adherent) throws TechnicalException;

	public List<Plongee> getListeAttenteForAdherent(Adherent adherent) throws TechnicalException;

	public void inscrireAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void inscrireAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void moveAdherentAttenteFromInscrit(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void supprimeAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void supprimeAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

}
