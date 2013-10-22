package com.asptt.plongee.resa.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.asptt.plongee.resa.exception.ResaException;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.InscriptionFilleul;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeService {

	Plongee rechercherPlongeeParId(Integer id);
	List<Plongee> rechercherPlongeeTout();
	List<Plongee> rechercherPlongeeProchainJour(Adherent adherent);
	List<Plongee> rechercherPlongeeAOuvrir(List<Plongee> plongees);
	List<Plongee> rechercherPlongeeOuverteTout(List<Plongee> plongees);
	List<Plongee> rechercherPlongeePourInscriptionAdherent(Adherent adherent);
	List<Plongee> rechercherPlongeesAdherentInscrit(Adherent adherent, int nbHours);
	List<Plongee> rechercherPlongeesOuvertesWithAttente(List<Plongee> plongees);
	List<Plongee> rechercherPlongees(Date date, String type);
	List<Adherent> rechercherInscriptions(Plongee plongee, String niveauPlongeur, String niveauEncadrement, String trie);
	List<Adherent> rechercherListeAttente(Plongee plongee);
	
	void creerPlongee(Plongee plongee) throws ResaException, TechnicalException;
	void supprimerPlongee(Plongee plongee) throws TechnicalException;
	void modifierPlongee(Plongee plongee);
	void creerFicheSecurite(FicheSecurite fs) throws ResaException, TechnicalException;
	void supprimerFicheSecurite(FicheSecurite fs) throws ResaException, TechnicalException;
	Integer getNbPlaceRestante(Plongee plongee);
	
	int isOkForResa(Plongee plongee, Adherent adherent) throws ResaException;
	boolean isOkForListeAttente(Plongee plongee, Adherent adherent) throws TechnicalException, ResaException;
	boolean isEnoughEncadrant(Plongee plongee) throws TechnicalException;
	boolean isOuverte(Plongee plongee);
	
	void inscrireAdherent(Plongee plongee, Adherent adherent, int typeMail) throws ResaException;
	void inscrireAdherent(Plongee plongee, Adherent adherent, Adherent filleul,
			int typeMail) throws ResaException, TechnicalException;
	void inscrireAdherentEnListeAttente(Plongee plongee, Adherent adherent, int typeMail) throws ResaException;
	void fairePasserAttenteAInscrit(Plongee plongee, Adherent adherent);

	void deInscrireAdherentForcee(Plongee plongee, Adherent adherent) throws ResaException;
	void deInscrireAdherent(Plongee plongee, Adherent adherent, int typeMail) throws ResaException;
	void deInscrireAdherentEnListeAttente(Plongee plongee, Adherent adherent) throws ResaException;
	void supprimerDeLaListeDattente(Plongee plongee, Adherent adherent, int indic)throws ResaException;
	
	void checkCertificatMedical(Adherent adherent, Plongee plongee) throws TechnicalException, ResaException;
	int getHeureOuverture(Calendar cal, Adherent adh, Plongee plongee);
	
	List<InscriptionFilleul> rechercherPlongeesFilleulInscrit(Adherent adherent,
			int nbHours) throws TechnicalException;

	List<Plongee> rechercherPlongeesSansFS(Date dateMin, Date dateMax);
}
