package com.asptt.plongee.resa.service;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface ReservationService {

	/**
	 * Indique si la plongée donnée peut être ouverte ou non.
	 * Une plongée peut être ouverte si dans les plongeurs il y a 1 directeur de plongée et un pilote OU si l'administrateur a forcé la plongée à l'ouverture. 
	 */
	boolean isOuverte(Plongee plongee);
	
	/**
	 * 
	 * @param plongee
	 * @param plongeur
	 * @throws MetierException si l'adhérent n'a pas le niveau requis pour participer 
	 */
	void inscrire(Plongee plongee, Adherent plongeur) throws MetierException;
	void desinscrire(Plongee plongee, Adherent plongeur) throws MetierException;
	
	
//	public boolean estValide(Plongee plongee);
	
}
