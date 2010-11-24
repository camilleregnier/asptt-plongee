package com.asptt.plongee.resa.dao;


import java.util.Date;
import java.util.List;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeDao extends GenericDao<Plongee, Integer>{

	List<Plongee> getPlongeesForFewDay(int aPartir, int nbjour) throws TechnicalException;
	
	List<Plongee> getPlongeesForEncadrant(int aPartir, int nbjour) throws TechnicalException;

	List<Plongee> getPlongeesForAdherent() throws TechnicalException;

	List<Plongee> getPlongeesWhereAdherentIsInscrit(Adherent adherent, int nbHours) throws TechnicalException;

	List<Plongee> getPlongeesWhithSameDate(Date date, String type) throws TechnicalException;

	List<Plongee> getListeAttenteForAdherent(Adherent adherent) throws TechnicalException;

	void inscrireAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	void inscrireAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

	void moveAdherentAttenteToInscrit(Plongee plongee, Adherent adherent) throws TechnicalException;

	void supprimeAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	void supprimeAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

}
