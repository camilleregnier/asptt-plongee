package com.asptt.plongee.resa.dao.inmemory;

import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscriptionFilleul;
import com.asptt.plongee.resa.model.Plongee;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlongeeInMemoryDao implements PlongeeDao {


	private List<Plongee> plongees;
	
	
	public PlongeeInMemoryDao() {
		plongees = new ArrayList<Plongee>();
	}

	public List<Plongee> findAllOuvertes() throws TechnicalException {
		throw new IllegalStateException("Cette fonction semble nécessité l'écriture de code métier. Suggestion : laisser la fonction au niveau du service PlongeeService");
	}

    @Override
	public Plongee create(Plongee obj) throws TechnicalException {
		if(plongees.contains(obj)){
			throw new TechnicalException("Plongee déjà créé");
		}
		plongees.add(obj);
		return obj;
	}

    @Override
	public void delete(Plongee obj) throws TechnicalException {
		plongees.remove(obj);		
	}

    @Override
	public List<Plongee> findAll() throws TechnicalException {
		List<Plongee> newPlongees = new ArrayList<Plongee>(plongees);
		return newPlongees;
	}

    @Override
	public Plongee findById(Integer id) throws TechnicalException {
		for (Plongee plongee : plongees) {
			if (plongee.getId().equals(id)) {
				return plongee;
			}
		}
		return null;
	}

    @Override
	public Plongee update(Plongee obj) throws TechnicalException {
		if(null == obj.getId()){
			throw new TechnicalException("Plongee sans identifiant");
		}

		return obj;
	}

	public List<Plongee> findAllForWeek() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
	public void inscrireAdherentPlongee(Plongee plongee, Adherent adherent)
			throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

    @Override
	public List<Plongee> getListeAttenteForAdherent(Adherent adherent)
		throws TechnicalException {
			// TODO Auto-generated method stub
		return null;
	}

	public List<Plongee> getPlongeesWhereAdherentIsInscrit(Adherent adherent)
		throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Plongee> getPlongeesForWeek() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
	public void inscrireAdherentAttente(Plongee plongee, Adherent adherent)
		throws TechnicalException {
		// TODO Auto-generated method stub
	}
	
	public void moveAdherentAttenteFromInscrit(Plongee plongee, Adherent adherent)
		throws TechnicalException {
		// TODO Auto-generated method stub
	}

    @Override
	public void sortirAdherentAttente(Plongee plongee, Adherent adherent)
			throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

    @Override
	public void supprimeAdherentPlongee(Plongee plongee, Adherent adherent)
			throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Plongee> getPlongeesWhithSameDate(Date date, String type)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Plongee> getPlongeesWhereAdherentIsInscrit(Adherent adherent,
			int nbHours) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveAdherentAttenteToInscrit(Plongee plongee, Adherent adherent)
			throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Plongee> getPlongeesForAdherent() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Plongee> getPlongeesForEncadrant(int aPartir, int nbjour)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void supprimerDeLaListeAttente(Plongee plongee, Adherent adherent,
			int indic) throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inscrireAdherentPlongee(Plongee plongee, Adherent adherent,
			Adherent filleul) throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<InscriptionFilleul> getPlongeesWhereFilleulIsInscrit(
			Adherent adherent, int nbHours) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}
}
