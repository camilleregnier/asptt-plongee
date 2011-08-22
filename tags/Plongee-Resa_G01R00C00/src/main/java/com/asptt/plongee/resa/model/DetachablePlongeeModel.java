package com.asptt.plongee.resa.model;

import org.apache.wicket.model.LoadableDetachableModel;

import com.asptt.plongee.resa.exception.TechnicalException;

public class DetachablePlongeeModel extends LoadableDetachableModel<Plongee> {

	private static final long serialVersionUID = 7356020767941072700L;
	private Plongee plongee;

	
	public DetachablePlongeeModel(Plongee plongee) {
		super();
		this.plongee = plongee;
	}

	@Override
	protected Plongee load()  throws TechnicalException{
		return plongee;
	}

}
