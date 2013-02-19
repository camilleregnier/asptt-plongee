package com.asptt.plongee.resa.model;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import com.asptt.plongee.resa.exception.TechnicalException;

@SuppressWarnings("serial")
public class PlongeeFilleulDataProvider implements IDataProvider<InscriptionFilleul> {

	List<InscriptionFilleul> plongeesDesFilleuls;
	

	public PlongeeFilleulDataProvider (List<InscriptionFilleul> filleuls) {
		this.plongeesDesFilleuls = filleuls;
	}


	@Override
	public Iterator<InscriptionFilleul> iterator(int first, int count){
			return plongeesDesFilleuls.subList(first, first+count).iterator();
	}

	@Override
	public int size() {
		return plongeesDesFilleuls.size();
	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModel<InscriptionFilleul> model(InscriptionFilleul filleul) throws TechnicalException{
		return new DetachableFilleulPlongeeModel(filleul);
	}

}
