package com.asptt.plongee.resa.model;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.service.AdherentService;

@SuppressWarnings("serial")
public class AdherentDataProvider implements IDataProvider<Adherent> {
	
	AdherentService adherentService;
	
	public AdherentDataProvider (AdherentService adherentService){
		this.adherentService = adherentService;
	}

	@Override
	public Iterator<Adherent> iterator(int first, int count) {
		return adherentService.rechercherAdherent(first, count).iterator();
	}

	@Override
	public int size() {
		if(null == adherentService.rechercherAdherentTout()){
			return 0;
		} else {			
			return adherentService.rechercherAdherentTout().size();
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
