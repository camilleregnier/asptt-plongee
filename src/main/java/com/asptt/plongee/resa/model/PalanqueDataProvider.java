package com.asptt.plongee.resa.model;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.TechnicalException;

@SuppressWarnings("serial")
public class PalanqueDataProvider implements IDataProvider<Palanque> {

	List<Palanque> palanques;
	

	public PalanqueDataProvider (List<Palanque> palanques) {
		this.palanques = palanques;
	}

	@Override
	public Iterator<Palanque> iterator(int first, int count){
			return palanques.subList(first, first+count).iterator();
	}

	@Override
	public int size() {
		return palanques.size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<Palanque> model(Palanque palanque) throws TechnicalException{
		return new DetachablePalanqueModel(palanque);
	}

}
