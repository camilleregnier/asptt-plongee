package com.asptt.plongee.resa.dao.inmemory;

import java.util.ArrayList;
import java.util.List;

import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public class PlongeeInMemoryDao implements PlongeeDao {


	private List<Plongee> plongees;
	
	
	public PlongeeInMemoryDao() {
		plongees = new ArrayList<Plongee>();
	}

	public List<Plongee> findAllOuvertes() throws TechnicalException {
		throw new IllegalStateException("Cette fonction semble nécessité l'écriture de code métier. Suggestion : laisser la fonction au niveau du service PlongeeService");
	}

	public Plongee create(Plongee obj) throws TechnicalException {
		if(plongees.contains(obj)){
			throw new TechnicalException("Plongee déjà créé");
		}
		plongees.add(obj);
		return obj;
	}

	public void delete(Plongee obj) throws TechnicalException {
		plongees.remove(obj);		
	}

	public List<Plongee> findAll() throws TechnicalException {
		List<Plongee> newPlongees = new ArrayList<Plongee>(plongees);
		return newPlongees;
	}

	public Plongee findById(Integer id) throws TechnicalException {
		for (Plongee plongee : plongees) {
			if (plongee.getId().equals(id)) {
				return plongee;
			}
		}
		return null;
	}

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

	public void inscrireAdherentPlongee(Plongee plongee, Adherent adherent)
			throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

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
	
	public void inscrireAdherentAttente(Plongee plongee, Adherent adherent)
		throws TechnicalException {
		// TODO Auto-generated method stub
	}
	
	public void moveAdherentAttenteFromInscrit(Plongee plongee, Adherent adherent)
		throws TechnicalException {
		// TODO Auto-generated method stub
	}

	public void supprimeAdherentAttente(Plongee plongee, Adherent adherent)
			throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

	public void supprimeAdherentPlongee(Plongee plongee, Adherent adherent)
			throws TechnicalException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Plongee> getPlongeesForFewDay(int nbjour)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}
}
