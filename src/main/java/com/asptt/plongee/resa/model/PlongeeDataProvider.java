package com.asptt.plongee.resa.model;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.TechnicalException;

@SuppressWarnings("serial")
public class PlongeeDataProvider implements IDataProvider<Plongee> {

	List<Plongee> plongees;
	

	public PlongeeDataProvider (List<Plongee> plongees) {
		this.plongees = plongees;
	}

	@Override
	public Iterator<Plongee> iterator(int first, int count){
			return plongees.subList(first, first+count).iterator();
	}

	@Override
	public int size() {
		return plongees.size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Plongee> model(Plongee plongee) throws TechnicalException{
		return new DetachablePlongeeModel(plongee);
	}

}
