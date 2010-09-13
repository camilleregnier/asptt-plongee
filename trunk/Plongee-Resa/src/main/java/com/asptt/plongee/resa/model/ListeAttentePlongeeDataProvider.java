package com.asptt.plongee.resa.model;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.service.AdherentService;
import com.asptt.plongee.resa.service.PlongeeService;

@SuppressWarnings("serial")
public class ListeAttentePlongeeDataProvider implements IDataProvider<Adherent> {
	
	List<Adherent> adherents ;
	
	public ListeAttentePlongeeDataProvider (List<Adherent> adherents){
		this.adherents = adherents;
	}

	@Override
	public Iterator<Adherent> iterator(int first, int count) {
		return adherents.subList(first, first+count).iterator();
	}

	@Override
	public int size() {
		return adherents.size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Adherent> model(Adherent adherent) {
		// TODO Auto-generated method stub
		
		return new DetachableAdherentModel(adherent);
	}

}
