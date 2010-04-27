package com.asptt.plongee.resa.dao.inmemory;

import java.util.ArrayList;
import java.util.List;

import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Plongee;

public class PlongeeInMemoryDao implements PlongeeDao {

	private List<Plongee> plongees;
	
	
	public PlongeeInMemoryDao() {
		plongees = new ArrayList<Plongee>();
	}

	@Override
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
}
