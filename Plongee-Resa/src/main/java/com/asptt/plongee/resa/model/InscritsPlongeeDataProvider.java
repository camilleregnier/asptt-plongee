package com.asptt.plongee.resa.model;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

@SuppressWarnings("serial")
public class InscritsPlongeeDataProvider implements IDataProvider<Adherent> {
	
	PlongeeService plongeeService;
	AdherentService adherentService;
	Plongee plongee;
	
	public InscritsPlongeeDataProvider (PlongeeService plongeeService, AdherentService adherentService, Plongee plongee){
		this.plongeeService = plongeeService;
		this.adherentService = adherentService;
		this.plongee = plongee;
	}

	@Override
	public Iterator<Adherent> iterator(int first, int count) {
		// TODO à remplacer par la bonne méthode de plongeeService
		return plongeeService.rechercherInscriptions(plongee).iterator();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if(null == plongeeService.rechercherInscriptions(plongee)){
			return 0;
		} else {			
			return plongeeService.rechercherInscriptions(plongee).size();
		}
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Adherent> model(Adherent adherent) {
		// TODO Auto-generated method stub
		
		return new DetachableAdherentModel(adherentService, adherent.getNumeroLicense());
	}

}
