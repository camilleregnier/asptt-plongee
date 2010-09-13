package com.asptt.plongee.resa.dao;


import java.util.Date;
import java.util.List;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeDao extends GenericDao<Plongee, Integer>{

	public List<Plongee> getPlongeesForFewDay( int nbjour) throws TechnicalException;
	
	public List<Plongee> getPlongeesWhereAdherentIsInscrit(Adherent adherent, int nbHours) throws TechnicalException;

	public List<Plongee> getPlongeesWhithSameDate(Date date, String type) throws TechnicalException;

	public List<Plongee> getListeAttenteForAdherent(Adherent adherent) throws TechnicalException;

	public void inscrireAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void inscrireAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void moveAdherentAttenteToInscrit(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void supprimeAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	public void supprimeAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

}
