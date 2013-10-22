package com.asptt.plongee.resa.model;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.service.PlongeeService;

@SuppressWarnings("serial")
public class PlongeesAdherentDataProvider implements IDataProvider<Plongee> {

	PlongeeService plongeeService;
	Adherent adherent;
	
	public PlongeesAdherentDataProvider (Adherent adherent, PlongeeService plongeeService){
		this.plongeeService = plongeeService;
		this.adherent = adherent;
	}

	@Override
	public Iterator<Plongee> iterator(int first, int count) throws TechnicalException{
		return plongeeService.rechercherPlongeesAdherentInscrit(adherent, 0).iterator();
	}

	@Override
	public int size() throws TechnicalException{
		return plongeeService.rechercherPlongeesAdherentInscrit(adherent, 0).size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Plongee> model(Plongee plongee) {
		// TODO Auto-generated method stub
		
		return new DetachablePlongeeModel(plongee);
	}

}
