package com.asptt.plongee.resa.model;

import com.asptt.plongee.resa.exception.TechnicalException;
import org.apache.wicket.model.LoadableDetachableModel;

public class DetachablePalanqueModel extends LoadableDetachableModel<Palanque> {

	private static final long serialVersionUID = 7356020767941072700L;
	private Palanque palanque;

	
	public DetachablePalanqueModel(Palanque palanque) {
		super();
		this.palanque = palanque;
	}

	@Override
	protected Palanque load()  throws TechnicalException{
		return palanque;
	}

}
