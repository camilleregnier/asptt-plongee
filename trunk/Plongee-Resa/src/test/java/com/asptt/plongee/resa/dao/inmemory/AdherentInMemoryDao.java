package com.asptt.plongee.resa.dao.inmemory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.authorization.strategies.role.Roles;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public class AdherentInMemoryDao implements AdherentDao{

	private List<Adherent> adherents;
	
	public AdherentInMemoryDao() {
		adherents = new ArrayList<Adherent>();
	}
	
	public List<String> getStrRoles(Adherent adherent) throws TechnicalException {
		adherent.getRoles();
		List<String> lstRoles = new ArrayList<String>();
		Iterator it = adherent.getRoles().iterator();
		while (it.hasNext()){
			lstRoles.add( (String) it.next());
		}
		return lstRoles;
	}

	public Adherent create(Adherent obj) throws TechnicalException {
		if(null == obj.getNumeroLicense()){
			throw new TechnicalException("Adherent sans N° de License");
		}
		
		if(adherents.contains(obj)){
			throw new TechnicalException("Adherent déjà créé");
		}
		adherents.add(obj);
		return obj;
	}

	public void delete(Adherent obj) throws TechnicalException {
		adherents.remove(obj);
	}

	public List<Adherent> findAll() throws TechnicalException {
		List<Adherent> copie = new ArrayList<Adherent>(adherents);
		return copie;
	}

	public Adherent findById(String id) throws TechnicalException {
		for(Adherent a : adherents){
			if(a.getNumeroLicense().equalsIgnoreCase(id)){
				return a;
			}
		}
		return null;
	}

	public Adherent update(Adherent obj) throws TechnicalException {
		// deja fait en memoire
		return obj;
	}

	public List<Adherent> getAdherents(int idPlongee) throws TechnicalException {
		return adherents;
	}

	public Adherent findDP(List<Adherent> adhrents) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	public Adherent findPilote(List<Adherent> adhrents)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Adherent> getAdherentsInscrits(Plongee plongee, String niveauPlongeur, String niveauEncadrement)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Adherent> getAdherentsWaiting(Plongee plongee)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIdRole(String libelle) throws TechnicalException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Adherent> getAdherentsLikeName(String name)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Adherent> getAdherentsActifs() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Adherent> getAdherentsInactifs() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Adherent> getAdherentsTous() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Adherent> getExternes() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Adherent findByIdAll(String id) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Adherent> getAdherentsLikeRole(String role)
			throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

}
