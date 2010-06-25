package com.asptt.plongee.resa.model;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.service.PlongeeService;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

@SuppressWarnings("serial")
public class PlongeeDataProvider implements IDataProvider<Plongee> {
	
	PlongeeService plongeeService;
	
	public PlongeeDataProvider (PlongeeService plongeeService){
		this.plongeeService = plongeeService;
	}

	@Override
	public Iterator<Plongee> iterator(int first, int count) {
		// TODO à remplacer par la bonne méthode de plongeeService
		return plongeeService.rechercherPlongeeTout().iterator();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return plongeeService.rechercherPlongeeTout().size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Plongee> model(Plongee plongee) {
		// TODO Auto-generated method stub
		
		return new DetachablePlongeeModel(plongeeService, plongee.getId());
	}

}
