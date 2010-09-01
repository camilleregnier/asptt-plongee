package com.asptt.plongee.resa.model;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;

@SuppressWarnings("serial")
public class ListeAttentePlongeeDataProvider implements IDataProvider<Adherent> {
	
	PlongeeService plongeeService;
	AdherentService adherentService;
	Plongee plongee;
	
	public ListeAttentePlongeeDataProvider (PlongeeService plongeeService, AdherentService adherentService, Plongee plongee){
		this.plongeeService = plongeeService;
		this.adherentService = adherentService;
		this.plongee = plongee;
	}

	@Override
	public Iterator<Adherent> iterator(int first, int count) {
		// TODO à remplacer par la bonne méthode de plongeeService
		return plongeeService.rechercherListeAttente(plongee).iterator();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if(null == plongeeService.rechercherListeAttente(plongee)){
			return 0;
		} else {			
			return plongeeService.rechercherListeAttente(plongee).size();
		}
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Adherent> model(Adherent adherent) {
		// TODO Auto-generated method stub
		
		return new DetachableAdherentModel(adherentService, adherent.getNumeroLicense(),adherent);
	}

}
