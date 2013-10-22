package com.asptt.plongee.resa.dao;


import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.FicheSecurite;
import com.asptt.plongee.resa.model.InscriptionFilleul;
import com.asptt.plongee.resa.model.Plongee;
import java.util.Date;
import java.util.List;

public interface PlongeeDao extends GenericDao<Plongee, Integer>{

	List<Plongee> getPlongeesForEncadrant(int aPartir, int nbjour) throws TechnicalException;

	List<Plongee> getPlongeesForAdherent() throws TechnicalException;

	List<Plongee> getPlongeesWhereAdherentIsInscrit(Adherent adherent, int nbHours) throws TechnicalException;

	List<Plongee> getPlongeesWhithSameDate(Date date, String type) throws TechnicalException;

	List<Plongee> getListeAttenteForAdherent(Adherent adherent) throws TechnicalException;

	void inscrireAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	void inscrireAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

	void moveAdherentAttenteToInscrit(Plongee plongee, Adherent adherent) throws TechnicalException;

	void supprimeAdherentPlongee(Plongee plongee, Adherent adherent) throws TechnicalException;

	void sortirAdherentAttente(Plongee plongee, Adherent adherent) throws TechnicalException;

	void supprimerDeLaListeAttente(Plongee plongee, Adherent adherent, int indic) throws TechnicalException;

	void inscrireAdherentPlongee(Plongee plongee, Adherent adherent,
			Adherent filleul) throws TechnicalException;

	List<InscriptionFilleul> getPlongeesWhereFilleulIsInscrit(Adherent adherent,
			int nbHours) throws TechnicalException;

	List<Plongee> getPlongeesWithoutFS(Date dateMin,Date dateMax) throws TechnicalException;

	void createFicheSecurite(FicheSecurite fs) throws TechnicalException;

	void deleteFicheSecurite(FicheSecurite fs) throws TechnicalException;
        
        public int getIdFicheSecurite(FicheSecurite fs) throws TechnicalException;
        
        public List<Integer> getIdPalanques(int iDfs) throws TechnicalException;
}
